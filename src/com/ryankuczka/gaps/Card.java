package com.ryankuczka.gaps;

public class Card {
	
	// instance variables
	private String mDrawableName;
	private int mRank;
	private int mSuit;
	private String mSuitString;
	private String mRankString;
	
	public Card(int rank, int suit) {
		mRank = rank;
		mSuit = suit;
		
		switch (mSuit) {
			case 1: mSuitString = "c"; break;
			case 2: mSuitString = "h"; break;
			case 3: mSuitString = "s"; break;
			case 4: mSuitString = "d"; break;
			default: mSuitString = ""; break;
		}
		switch (mRank) {
			case 1: mRankString = "a"; break;
			case 11: mRankString = "j"; break;
			case 12: mRankString = "q"; break;
			case 13: mRankString = "k"; break;
			default: mRankString = "" + mRank; break;
		}
		
		if (mRank == -1) {
			mDrawableName = "blank";
		}
		else {
			mDrawableName = mSuitString + mRankString;
		}
	}
	
	public String getDrawableName() {
		return mDrawableName;
	}
	
	public int getRank() {
		return mRank;
	}
	
	public int getSuit() {
		return mSuit;
	}
}
