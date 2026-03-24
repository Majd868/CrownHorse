package com.crownhorse.app.horses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Horse;
import com.crownhorse.app.repository.HorseRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HorsesFragment extends Fragment {

    private RecyclerView recyclerView;
    private HorseAdapter adapter;
    private List<Horse> horses = new ArrayList<>();
    private TextView tvEmpty;
    private View progressBar;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_horses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);
        fab = view.findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HorseAdapter(horses,
                horse -> {
                    Intent intent = new Intent(getContext(), AddEditHorseActivity.class);
                    intent.putExtra("horseId", horse.getHorseId());
                    startActivity(intent);
                },
                horse -> {
                    new HorseRepository().deleteHorse(horse.getHorseId(), new HorseRepository.Callback<>() {
                        @Override
                        public void onSuccess(Void result) {
                            horses.remove(horse);
                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (getView() != null)
                                Snackbar.make(getView(), e.getMessage() != null ? e.getMessage() : "Error",
                                        Snackbar.LENGTH_SHORT).show();
                        }
                    });
                });
        recyclerView.setAdapter(adapter);

        // Only owners can add horses
        SharedPreferences prefs = requireActivity().getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "owner");
        fab.setVisibility("owner".equals(role) ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddEditHorseActivity.class)));

        loadHorses();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHorses();
    }

    private void loadHorses() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        progressBar.setVisibility(View.VISIBLE);
        new HorseRepository().getHorsesByOwner(uid, new HorseRepository.Callback<>() {
            @Override
            public void onSuccess(List<Horse> result) {
                progressBar.setVisibility(View.GONE);
                horses.clear();
                if (result != null) horses.addAll(result);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                if (getView() != null)
                    Snackbar.make(getView(), e.getMessage() != null ? e.getMessage() : "Error",
                            Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        tvEmpty.setVisibility(horses.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(horses.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
