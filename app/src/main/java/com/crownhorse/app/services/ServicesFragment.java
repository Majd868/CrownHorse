package com.crownhorse.app.services;

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
import com.crownhorse.app.models.Service;
import com.crownhorse.app.repository.ServiceRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> services = new ArrayList<>();
    private TextView tvEmpty;
    private View progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceAdapter(services, service -> {
            Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
            intent.putExtra("serviceId", service.getServiceId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = requireActivity().getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "owner");
        fab.setVisibility("provider".equals(role) ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEditServiceActivity.class)));

        loadServices();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadServices();
    }

    private void loadServices() {
        progressBar.setVisibility(View.VISIBLE);
        new ServiceRepository().getAllServices(new ServiceRepository.Callback<>() {
            @Override
            public void onSuccess(List<Service> result) {
                progressBar.setVisibility(View.GONE);
                services.clear();
                if (result != null) services.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(services.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(services.isEmpty() ? View.GONE : View.VISIBLE);
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
}
