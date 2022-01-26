package com.mambo.rafiki.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mambo.rafiki.R;
import com.mambo.rafiki.data.entities.Reason;
import com.mambo.rafiki.databinding.LayoutReasonBottomSheetBinding;
import com.mambo.rafiki.ui.viewmodels.DecisionViewModel;
import com.mambo.rafiki.utils.DecisionUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;
import com.mambo.rafiki.utils.StringUtils;

public class ReasonBottomSheet extends BottomSheetDialogFragment {

    public static final int PRO_ID = 200;
    public static final int CON_ID = 201;

    private int REASON_TYPE;
    private ReasonListener listener;

    private LayoutReasonBottomSheetBinding binding;
    private SharedPrefsUtil prefsUtil;

    private Reason reason;
    private DecisionViewModel viewModel;

    public ReasonBottomSheet(int REASON_TYPE, ReasonListener reasonListener) {
        this.REASON_TYPE = REASON_TYPE;
        this.listener = reasonListener;
    }

    public ReasonBottomSheet(int REASON_TYPE, Reason reason, ReasonListener reasonListener) {
        this.REASON_TYPE = REASON_TYPE;
        this.reason = reason;
        this.listener = reasonListener;
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

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_reason_bottom_sheet, null, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefsUtil = SharedPrefsUtil.getInstance(requireActivity().getApplication());

        String text;

        if (reason == null) {
            text = "Add ";
        } else {
            text = " Update ";

            binding.edtAdd.setText(reason.getReason());
            binding.slider.setValue(reason.getWeight());
            binding.tvIntensity.setText(DecisionUtils.getEmojiWithDifference(reason.getWeight()));
        }

        switch (REASON_TYPE) {
            case PRO_ID:
                binding.tvTitle.setText(text + "Pro");
                binding.slider.addOnChangeListener((slider, value, fromUser) ->
                        binding.tvIntensity.setText(DecisionUtils.getEmojiPositiveIntensity((int) value)));

                break;
            case CON_ID:
                binding.tvTitle.setText(text + "Con");
                binding.slider.addOnChangeListener((slider, value, fromUser) ->
                        binding.tvIntensity.setText(DecisionUtils.getEmojiNegativeIntensity((int) value)));

                break;
            default:
                break;
        }

        binding.constAdd.setOnClickListener(v -> {
            saveReason();
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void saveReason() {

        String content = StringUtils.getStringFrom(binding.edtAdd.getText().toString());
        int weight = (int) binding.slider.getValue();

        if (TextUtils.isEmpty(content)) {
            binding.inputLayoutAdd.setError("Invalid Reason");
            return;
        }

        Reason reason = new Reason();

        reason.setReason(content);
        reason.setWeight(weight);

        listener.onReasonSaved(reason);
        dismiss();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public interface ReasonListener {
        void onReasonSaved(Reason reason);
    }

}
