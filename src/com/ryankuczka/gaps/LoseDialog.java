package com.ryankuczka.gaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class LoseDialog extends DialogFragment {

	public interface LoseDialogListener {
		public void onLoseDialogPositiveClick(DialogFragment dialog);
		public void onLoseDialogNegativeClick(DialogFragment dialog);
	}

	LoseDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// verify that activity has implemented listener
		try {
			mListener = (LoseDialogListener) activity;
		}
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " does not implement LoseDialogListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.you_lose);
		builder.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// shuffle
				mListener.onLoseDialogPositiveClick(LoseDialog.this);
			}
		});
		builder.setNegativeButton(R.string.home_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// cancel	
				mListener.onLoseDialogNegativeClick(LoseDialog.this);
			}
		});
		return builder.create();
	}

}
