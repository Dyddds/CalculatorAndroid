package com.otz.calculator.ui.base;

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
import com.otz.calculator.databinding.FragmentBaseBinding;

public class BaseFragment extends Fragment {

    private BaseViewModel baseViewModel;
    private FragmentBaseBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        baseViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);

        binding = FragmentBaseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBase;
        baseViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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