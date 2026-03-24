package com.crownhorse.app.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.crownhorse.app.R;
import com.crownhorse.app.auth.SignInActivity;
import com.crownhorse.app.models.User;
import com.crownhorse.app.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private CircleImageView ivAvatar;
    private TextView tvName, tvRole, tvLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvRole = view.findViewById(R.id.tvRole);
        tvLocation = view.findViewById(R.id.tvLocation);
        Button btnEdit = view.findViewById(R.id.btnEdit);
        Button btnSignOut = view.findViewById(R.id.btnSignOut);

        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(getContext(), EditProfileActivity.class)));

        btnSignOut.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                new UserRepository().updatePresence(uid, false);
            }
            FirebaseAuth.getInstance().signOut();
            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(getContext(), SignInActivity.class));
            requireActivity().finish();
        });

        loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        new UserRepository().getUser(uid, new UserRepository.Callback<>() {
            @Override
            public void onSuccess(User user) {
                if (user == null || getView() == null) return;

                tvName.setText(user.getName() != null ? user.getName() : "");
                tvRole.setText(user.getRole() != null ? user.getRole() : "");
                String loc = (user.getCity() != null ? user.getCity() : "")
                        + (user.getCountry() != null ? ", " + user.getCountry() : "");
                tvLocation.setText(loc.trim());

                if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.getPhotoUrl())
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(ivAvatar);
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_profile_placeholder);
                }

                // Save role to prefs
                requireActivity().getSharedPreferences("crownhorse_prefs", MODE_PRIVATE)
                        .edit().putString("userRole", user.getRole()).apply();
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }
}
