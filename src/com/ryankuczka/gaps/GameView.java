package com.ryankuczka.gaps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View {	
	private Drawable[][] mTableau = new Drawable[4][13];
	private boolean[][] mHighlight = new boolean[4][13];
	
	int paddingLeft;
	int paddingTop;
	int paddingRight;
	int paddingBottom;
	
	int cardWidth;
	int cardHeight;
	int cardBgWidth;
	int cardBgHeight;
	
	Drawable noHighlight = new ColorDrawable(Color.WHITE);
	Drawable redHighlight = new ColorDrawable(Color.BLUE);
	
	public GameView(Context context) {
		super(context);
		init(null, 0);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {

		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				mTableau[row][col] = getResources().getDrawable(R.drawable.blank);
				mHighlight[row][col] = false;
			}
		}

		paddingLeft = this.getPaddingLeft();
		paddingTop = this.getPaddingTop();
		paddingRight = this.getPaddingRight();
		paddingBottom = this.getPaddingBottom();
				
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		cardWidth = (getWidth() - paddingLeft - paddingRight - 6 * 13) / 13;
		cardHeight = (getHeight() - paddingTop - paddingBottom - 6 * 4) / 4;
		cardBgWidth = (getWidth() - paddingLeft - paddingRight) / 13;
		cardBgHeight = (getHeight() - paddingTop - paddingBottom) / 4;
		
		int topBound = paddingTop + 3;
		int leftBound = paddingLeft + 3;
		int leftBgBound = paddingLeft;
		int topBgBound = paddingTop;
		
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				if (mHighlight[row][col]) {
					redHighlight.setBounds(leftBgBound, topBgBound, leftBgBound + cardBgWidth, topBgBound + cardBgHeight);
					redHighlight.draw(canvas);
				}
				else {
					noHighlight.setBounds(leftBgBound, topBgBound, leftBgBound + cardBgWidth, topBgBound + cardBgHeight);
					noHighlight.draw(canvas);
				}
				
				leftBgBound += cardBgWidth;
			}
			
			topBgBound += cardBgHeight;
			leftBgBound = paddingLeft;
		}
		for (int row = 0; row < 4; row++) {			
			for (int col = 0; col < 13; col++) {
				mTableau[row][col].setBounds(leftBound, topBound, leftBound + cardWidth, topBound + cardHeight);
				mTableau[row][col].draw(canvas);
				
				leftBound += cardWidth + 6;
			}
			topBound += cardHeight + 6;
			leftBound = paddingLeft + 3;
		}
	}
	
	public void setCardAtIndex(int row, int col, Card card) {
		int drawableId = getResources().getIdentifier(card.getDrawableName(), "drawable", "com.ryankuczka.gaps");
		Drawable drawableCard = getResources().getDrawable(drawableId);
		mTableau[row][col] = drawableCard;
	}
	
	public void setIndexesToHighlight(int[][] indexes) {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 13; col++) {
				mHighlight[row][col] = false;
			}
		}
		for (int[] index : indexes) {
			mHighlight[index[0]][index[1]] = true;
		}
	}

}
