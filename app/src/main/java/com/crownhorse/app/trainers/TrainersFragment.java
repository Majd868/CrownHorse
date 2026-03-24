package com.crownhorse.app.trainers;

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
import com.crownhorse.app.models.Trainer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TrainersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TrainerAdapter adapter;
    private List<Trainer> trainers = new ArrayList<>();
    private TextView tvEmpty;
    private View progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false); // reuse layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);
        view.findViewById(R.id.fab).setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TrainerAdapter(trainers, trainer -> {});
        recyclerView.setAdapter(adapter);
        loadTrainers();
    }

    private void loadTrainers() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("role", "provider").get()
                .addOnSuccessListener(snapshot -> {
                    progressBar.setVisibility(View.GONE);
                    trainers.clear();
                    for (var doc : snapshot.getDocuments()) {
                        Trainer t = new Trainer();
                        t.setTrainerId(doc.getId());
                        t.setName(doc.getString("name") != null ? doc.getString("name") : "");
                        t.setSpecialization(doc.getString("specialization") != null
                                ? doc.getString("specialization") : "");
                        t.setPhotoUrl(doc.getString("photoUrl") != null
                                ? doc.getString("photoUrl") : "");
                        trainers.add(t);
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(trainers.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(trainers.isEmpty() ? View.GONE : View.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                });
    }
}
