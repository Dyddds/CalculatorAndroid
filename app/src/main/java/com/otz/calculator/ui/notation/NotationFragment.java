package com.otz.calculator.ui.notation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.otz.calculator.R;
import com.otz.calculator.databinding.FragmentNotationBinding;

public class NotationFragment extends Fragment {

    private NotationViewModel notationViewModel;
    private FragmentNotationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notationViewModel =
                new ViewModelProvider(this).get(NotationViewModel.class);

        binding = FragmentNotationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotation;
        notationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}