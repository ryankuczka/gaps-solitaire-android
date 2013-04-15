package com.ryankuczka.gaps;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class PlayGameActivity extends Activity 
		implements OnTouchListener, NoMovesDialog.NoMovesDialogListener,
				   WinDialog.WinDialogListener, LoseDialog.LoseDialogListener {
	
	private Card[][] mDeck = new Card[4][13];
	private boolean[][] mFinalPos = new boolean[4][13];
	
	private GameView gv;
	private TextView mShufflesRemainingTv;
	private int[][] mBlankIndexes = new int[4][2];
	private int[][] mMovableIndexes = new int[4][2];
	private int[][] mTwosIndexes = new int[4][2];
	private boolean mWin = false;
	private boolean mNoMoves = false;
	private int mShufflesRemaining = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_play_game);
		
		gv = (GameView) findViewById(R.id.game_view);
		mShufflesRemainingTv = (TextView) findViewById(R.id.shuffles_remaining);
		
		// build a deck of cards
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				if (col == 0) {
					mDeck[row][col] = new Card(-1, -1);
				}
				else {
					mDeck[row][col] = new Card(col + 1, row + 1);
				}
				gv.setCardAtIndex(row, col, mDeck[row][col]);
				gv.setOnTouchListener(this);
				mFinalPos[row][col] = false;
			}
		}
		update(true, true);
	}
	
	public void update(boolean shuffle, boolean initial) {
		// shuffle if told to do so
		if (shuffle) {
			shuffle(initial);
		}
		
		// update card index arrays
		findBlankCards();
		findMovableCards();
		findTwos();
		mWin = checkWin();
		mNoMoves = checkNoMoves();
		
		// update game view
		sendMovableCards();
		updateTableau();
		gv.invalidate();
	}
	
	public void newGame() {
		mShufflesRemaining = 4;
		update(true, true);
	}
	
	// main interactions with view
	public boolean onTouch(View view, MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		int[] touchIndex = getTouchIndex(x, y);
		if (touchIndex[0] == -1) {
			return true;
		}
		for (int[] index : mMovableIndexes) {
			if (index[0] == -2) {
				for (int[] twoIndex : mTwosIndexes) {
					if (twoIndex[0] == touchIndex[0] && twoIndex[1] == touchIndex[1]) {
						moveCard(twoIndex);
						return true;
					}
				}
			}
			else if (index[0] == touchIndex[0] && index[1] == touchIndex[1]) {
				moveCard(index);
				return true;
			}
		}
		return true;
	}
	
	public void goHome(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void shuffle(View view) {
		update(true, false);
	}
	
	// main interactions with mDeck (i.e. movement of cards)
	public void shuffle(boolean initial) {
		if (mShufflesRemaining == 0 && !initial) {
			LoseDialog dialog = new LoseDialog();
			dialog.show(getFragmentManager(), "lose");
		}
		if (!initial) {
			mShufflesRemaining--;
		}
		mShufflesRemainingTv.setText("" + mShufflesRemaining);
		// make sure the final positions have been updated
		updateFinalPos();
		// go through the deck and swap with a random card, 7 times to be good
		for (int i = 0; i < 7; i++) {
			for (int row = 0; row < 4; row++) {
				for (int col = 0; col < 13; col++) {
					// if the current card is correct skip it, unless it is initial shuffle
					if (mFinalPos[row][col] && !initial) {
						continue;
					}
					
					Card currentCard = mDeck[row][col];
					
					
					int randomRow = (int)Math.floor(Math.random() * 4);
					int randomCol = (int)Math.floor(Math.random() * 13);
					while (mFinalPos[randomRow][randomCol] && !initial) {
						randomRow = (int)Math.floor(Math.random() * 4);
						randomCol = (int)Math.floor(Math.random() * 13);
					}
					
					Card otherCard = mDeck[randomRow][randomCol];
					
					mDeck[row][col] = otherCard;
					mDeck[randomRow][randomCol] = currentCard;
				}
			}
		}
	}
	
	public void moveCard(int[] index) {
		Card cardToMove = mDeck[index[0]][index[1]];
		// find the correct blank index and swap the cards
		for (int[] blankIndex : mBlankIndexes) {
			if (cardToMove.getRank() == 2 && blankIndex[1] == 0) {
				mDeck[index[0]][index[1]] = mDeck[blankIndex[0]][blankIndex[1]];
				mDeck[blankIndex[0]][blankIndex[1]] = cardToMove;
			}
			else if (blankIndex[1] == 0) {
				continue;
			}
			else {
				Card cardToLeft = mDeck[blankIndex[0]][blankIndex[1] - 1];
				if (cardToMove.getSuit() == cardToLeft.getSuit() && cardToMove.getRank() == cardToLeft.getRank() + 1) {
					mDeck[index[0]][index[1]] = mDeck[blankIndex[0]][blankIndex[1]];
					mDeck[blankIndex[0]][blankIndex[1]] = cardToMove;
				}
			}
		}
		
		// update everything
		findBlankCards();
		findMovableCards();
		findTwos();
		mWin = checkWin();
		mNoMoves = checkNoMoves();
		
		// update the view
		sendMovableCards();
		updateTableau();
		gv.invalidate();
		
		// raise dialogs
		if (mWin) {
			WinDialog dialog = new WinDialog();
			dialog.show(getFragmentManager(), "win");
		}
		else if (mNoMoves && mShufflesRemaining == 0) {
			LoseDialog dialog = new LoseDialog();
			dialog.show(getFragmentManager(), "lose");
		}
		else if (mNoMoves) {
			NoMovesDialog dialog = new NoMovesDialog();
			dialog.show(getFragmentManager(), "noMoves");
		}
	}
	
	
	// methods to find indexes, cards
	public void findBlankCards() {
		int ctr = 0;
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				Card currentCard = mDeck[row][col];
				if (currentCard.getRank() == -1) {
					mBlankIndexes[ctr][0] = row;
					mBlankIndexes[ctr][1] = col;
					ctr++;
				}
			}
		}
	}
	
	public void findMovableCards() {
		findBlankCards();
		// iterate over blank indexes
		int ctr = 0;
		for (int[] index : mBlankIndexes) {
			// if the twos are movable, mark as (-2, -2)
			if (index[1] == 0) {
				mMovableIndexes[ctr][0] = -2;
				mMovableIndexes[ctr][1] = -2;
				ctr++;
				continue;
			}
			Card cardToLeft = mDeck[index[0]][index[1] - 1];
			// if there is no possible move, mark as (-1, -1)
			if (cardToLeft.getRank() == -1 || cardToLeft.getRank() == 13) {
				mMovableIndexes[ctr][0] = -1;
				mMovableIndexes[ctr][1] = -1;
				ctr++;
				continue;
			}
			// search for the index of the movable card
			for (int row = 0; row < 4; row++) {
				for (int col = 0; col < 13; col++) {
					Card currentCard = mDeck[row][col];
					if (currentCard.getSuit() != cardToLeft.getSuit()) {
						continue;
					}
					else if (currentCard.getRank() != cardToLeft.getRank() + 1) {
						continue;
					}
					else {
						mMovableIndexes[ctr][0] = row;
						mMovableIndexes[ctr][1] = col;
						ctr++;
					}
				}
			}
		}
	}
	
	public void findTwos() {
		int numTwos = 0;
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				Card currentCard = mDeck[row][col];
				if (currentCard.getRank() == 2) {
					mTwosIndexes[numTwos][0] = row;
					mTwosIndexes[numTwos][1] = col;
					numTwos++;
				}
			}
		}
	}
	
	public int[] getTouchIndex(int x, int y) {
		int width = gv.getWidth();
		int height = gv.getHeight();
		int padLeft = gv.getPaddingLeft();
		int padTop = gv.getPaddingTop();		
		
		if (x < padLeft || x > width - padLeft) {
			return new int[] {-1, -1};
		}
		else if (y < padTop || y > height - padTop) {
			return new int[] {-1, -1};
		}
		
		int colWidth = (width - 2 * padLeft) / 13;
		int rowHeight = (height - 2 * padTop) / 4;
		int col = 1;
		int row = 1;
		
		while (padLeft + col * colWidth < x) {
			col++;
		}
		while (padTop + row * rowHeight < y) {
			row++;
		}
		
		return new int[] {row - 1, col - 1};		
	}
	
	
	// methods to send things to the view
	public void sendMovableCards() {
		findMovableCards();
		int numMovable = 0;
		boolean includeTwos = false;
		for (int[] index : mMovableIndexes) {
			if (index[0] == -2) {
				// don't re-add twos
				if (includeTwos) {
					continue;
				}
				numMovable += 4;
				includeTwos = true;
			}
			else if (index[0] == -1) {
				continue;
			}
			else {
				numMovable++;
			}
		}
		
		int[][] movableIndexes = new int[numMovable][2];
		int ctr = 0;
		if (includeTwos) {
			for (int[] index : mTwosIndexes) {
				movableIndexes[ctr][0] = index[0];
				movableIndexes[ctr][1] = index[1];
				ctr++;
			}
		}
		for (int[] index : mMovableIndexes) {
			if (index[0] == -1 || index[0] == -2) {
				continue;
			}
			else {
				movableIndexes[ctr][0] = index[0];
				movableIndexes[ctr][1] = index[1];
				ctr++;
			}
		}
		
		gv.setIndexesToHighlight(movableIndexes);
	}
	

	public void updateTableau() {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				gv.setCardAtIndex(row, col, mDeck[row][col]);
			}
		}
	}
	
	// methods to check game states
	public void updateFinalPos() {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				if (col == 0) {
					if (mDeck[row][col].getRank() == 2) {
						mFinalPos[row][col] = true;
						continue;
					}
					else {
						mFinalPos[row][col] = false;
						continue;
					}
				}
				
				Card currentCard = mDeck[row][col];
				Card cardToLeft = mDeck[row][col - 1];
		
				if (currentCard.getRank() != col + 2) {
					mFinalPos[row][col] = false;
				}
				else if (currentCard.getSuit() != cardToLeft.getSuit()) {
					mFinalPos[row][col] = false;
				}
				else if (currentCard.getRank() != cardToLeft.getRank() + 1) {
					mFinalPos[row][col] = false;
				}
				else {
					mFinalPos[row][col] = true;
				}
			}
		}
	}
	
	public boolean checkWin() {
		for (int row = 0; row < 4; row++) {
			// we only need to check the first 12 cols
			for (int col = 0; col < 12; col++) {
				if (!mFinalPos[row][col]) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkNoMoves() {
		for (int[] index : mMovableIndexes) {
			if (index[0] != -1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onNoMovesDialogPositiveClick(DialogFragment dialog) {
		update(true, false);
		dialog.dismiss();
	}

	@Override
	public void onNoMovesDialogNegativeClick(DialogFragment dialog) {
		dialog.dismiss();		
	}

	@Override
	public void onWinDialogPositiveClick(DialogFragment dialog) {
		newGame();
		dialog.dismiss();
	}

	@Override
	public void onWinDialogNegativeClick(DialogFragment dialog) {
		goHome(findViewById(R.id.home_button));
		dialog.dismiss();
	}

	@Override
	public void onLoseDialogPositiveClick(DialogFragment dialog) {
		newGame();
		dialog.dismiss();
	}

	@Override
	public void onLoseDialogNegativeClick(DialogFragment dialog) {
		goHome(findViewById(R.id.home_button));
		dialog.dismiss();
	}
	
}