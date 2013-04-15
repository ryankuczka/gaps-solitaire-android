package com.ryankuczka.gaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NoMovesDialog extends DialogFragment {
	
	public interface NoMovesDialogListener {
		public void onNoMovesDialogPositiveClick(DialogFragment dialog);
		public void onNoMovesDialogNegativeClick(DialogFragment dialog);
	}
	
	NoMovesDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// verify that activity has implemented listener
		try {
			mListener = (NoMovesDialogListener) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " does not implement NoMovesDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.no_moves);
		builder.setPositiveButton(R.string.shuffle_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// shuffle
				mListener.onNoMovesDialogPositiveClick(NoMovesDialog.this);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancel	
				mListener.onNoMovesDialogNegativeClick(NoMovesDialog.this);
			}
		});
		return builder.create();
	}
}
