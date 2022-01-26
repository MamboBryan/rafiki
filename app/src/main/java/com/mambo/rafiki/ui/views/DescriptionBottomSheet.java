package com.mambo.rafiki.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mambo.rafiki.R;
import com.mambo.rafiki.databinding.LayoutBottomSheetAddBinding;

public class DescriptionBottomSheet extends BottomSheetDialogFragment {

    private LayoutBottomSheetAddBinding binding;
    private DescriptionListener listener;

    public DescriptionBottomSheet(DescriptionListener descriptionListener) {
        //empty
        this.listener = descriptionListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                BottomSheetDialog d = (BottomSheetDialog) dialog1;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_bottom_sheet_add, null, false);

        binding.edtAdd.requestFocus();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.inputLayoutAdd.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDescription();
            }
        });

        binding.edtAdd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateDescription();
                    return true;
                }

                return false;
            }
        });

        binding.edtAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setEditTextEndIcon();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        showKeyboard();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setEditTextEndIcon() {

        String decision = binding.edtAdd.getText().toString();

        if (!TextUtils.isEmpty(decision)) {
            binding.inputLayoutAdd.setEndIconDrawable(R.drawable.ic_baseline_send_24);
        } else {
            binding.inputLayoutAdd.setEndIconDrawable(R.drawable.ic_baseline_add_24);
        }

    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void updateDescription() {

        String content = binding.edtAdd.getText().toString();

        if (!TextUtils.isEmpty(content)) {

            binding.inputLayoutAdd.setEnabled(false);
            listener.onDescriptionSaved(content);
            dismiss();

        } else {

            setEditTextEndIcon();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (binding != null)
            binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public interface DescriptionListener {
        void onDescriptionSaved(String description);
    }

}
