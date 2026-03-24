package com.crownhorse.app.bookings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Booking;
import com.crownhorse.app.models.Horse;
import com.crownhorse.app.models.Service;
import com.crownhorse.app.models.Trainer;
import com.crownhorse.app.repository.BookingRepository;
import com.crownhorse.app.repository.HorseRepository;
import com.crownhorse.app.repository.ServiceRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateBookingActivity extends AppCompatActivity {

    private Spinner spHorse, spService, spTrainer;
    private Button btnDateTime, btnConfirm;
    private TextInputEditText etDateTime;
    private View progressBar;

    private List<Horse> horses = new ArrayList<>();
    private List<Service> services = new ArrayList<>();
    private List<Trainer> trainers = new ArrayList<>();
    private Calendar selectedDateTime = Calendar.getInstance();
    private boolean dateTimeSelected = false;

    private String preselectedServiceId;
    private String preselectedProviderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spHorse = findViewById(R.id.spHorse);
        spService = findViewById(R.id.spService);
        spTrainer = findViewById(R.id.spTrainer);
        btnDateTime = findViewById(R.id.btnDateTime);
        btnConfirm = findViewById(R.id.btnConfirm);
        etDateTime = findViewById(R.id.etDateTime);
        progressBar = findViewById(R.id.progressBar);

        preselectedServiceId = getIntent().getStringExtra("serviceId");
        preselectedProviderId = getIntent().getStringExtra("providerId");

        loadData();
        btnDateTime.setOnClickListener(v -> pickDateTime());
        btnConfirm.setOnClickListener(v -> createBooking());
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        new HorseRepository().getHorsesByOwner(uid, new HorseRepository.Callback<>() {
            @Override
            public void onSuccess(List<Horse> result) {
                horses.clear();
                if (result != null) horses.addAll(result);
                List<String> names = new ArrayList<>();
                for (Horse h : horses) names.add(h.getName());
                spHorse.setAdapter(new ArrayAdapter<>(CreateBookingActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names));
            }
            @Override public void onFailure(Exception e) {}
        });

        new ServiceRepository().getAllServices(new ServiceRepository.Callback<>() {
            @Override
            public void onSuccess(List<Service> result) {
                services.clear();
                if (result != null) services.addAll(result);
                List<String> names = new ArrayList<>();
                for (Service s : services) names.add(s.getName());
                spService.setAdapter(new ArrayAdapter<>(CreateBookingActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names));
                // Pre-select if coming from service detail
                if (preselectedServiceId != null) {
                    for (int i = 0; i < services.size(); i++) {
                        if (services.get(i).getServiceId().equals(preselectedServiceId)) {
                            spService.setSelection(i);
                            break;
                        }
                    }
                }
            }
            @Override public void onFailure(Exception e) {}
        });

        // Load trainers (providers with trainer role)
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("role", "provider").get()
                .addOnSuccessListener(snapshot -> {
                    trainers.clear();
                    List<String> names = new ArrayList<>();
                    names.add(getString(R.string.no_trainer));
                    trainers.add(null); // placeholder for "no trainer"
                    for (var doc : snapshot.getDocuments()) {
                        Trainer t = doc.toObject(Trainer.class);
                        if (t == null) {
                            t = new Trainer();
                            t.setTrainerId(doc.getId());
                            t.setName(doc.getString("name") != null ? doc.getString("name") : "");
                        }
                        trainers.add(t);
                        names.add(t.getName());
                    }
                    spTrainer.setAdapter(new ArrayAdapter<>(CreateBookingActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, names));
                });
    }

    private void pickDateTime() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (dp, y, m, d) -> {
            selectedDateTime.set(y, m, d);
            new TimePickerDialog(this, (tp, h, min) -> {
                selectedDateTime.set(Calendar.HOUR_OF_DAY, h);
                selectedDateTime.set(Calendar.MINUTE, min);
                dateTimeSelected = true;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                etDateTime.setText(sdf.format(selectedDateTime.getTime()));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void createBooking() {
        if (horses.isEmpty()) {
            Toast.makeText(this, R.string.error_no_horses, Toast.LENGTH_SHORT).show(); return;
        }
        if (services.isEmpty()) {
            Toast.makeText(this, R.string.error_no_services, Toast.LENGTH_SHORT).show(); return;
        }
        if (!dateTimeSelected) {
            Toast.makeText(this, R.string.error_select_datetime, Toast.LENGTH_SHORT).show(); return;
        }

        int horseIdx = spHorse.getSelectedItemPosition();
        int serviceIdx = spService.getSelectedItemPosition();
        int trainerIdx = spTrainer.getSelectedItemPosition();

        Horse selectedHorse = horses.get(horseIdx);
        Service selectedService = services.get(serviceIdx);
        Trainer selectedTrainer = trainerIdx > 0 && trainerIdx < trainers.size()
                ? trainers.get(trainerIdx) : null;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        Booking booking = new Booking();
        booking.setOwnerId(uid);
        booking.setProviderId(selectedService.getProviderId());
        booking.setHorseId(selectedHorse.getHorseId());
        booking.setServiceId(selectedService.getServiceId());
        booking.setTrainerId(selectedTrainer != null ? selectedTrainer.getTrainerId() : null);
        booking.setDatetime(selectedDateTime.getTimeInMillis());
        booking.setStatus("pending");
        booking.setCreatedAt(System.currentTimeMillis());

        setLoading(true);
        new BookingRepository().createBooking(booking, new BookingRepository.Callback<>() {
            @Override public void onSuccess(String id) { setLoading(false); finish(); }
            @Override public void onFailure(Exception e) {
                setLoading(false);
                Toast.makeText(CreateBookingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnConfirm.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
