package com.ryankuczka.gaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class WinDialog extends DialogFragment {
	
	public interface WinDialogListener {
		public void onWinDialogPositiveClick(DialogFragment dialog);
		public void onWinDialogNegativeClick(DialogFragment dialog);
	}

	WinDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// verify that activity has implemented listener
		try {
			mListener = (WinDialogListener) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " does not implement WinDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.you_win);
		builder.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// shuffle
				mListener.onWinDialogPositiveClick(WinDialog.this);
			}
		});
		builder.setNegativeButton(R.string.home_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancel	
				mListener.onWinDialogNegativeClick(WinDialog.this);
			}
		});
		return builder.create();
	}
}
