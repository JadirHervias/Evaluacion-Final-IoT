package pe.edu.tecsup.evaluacionfinal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pe.edu.tecsup.evaluacionfinal.R;
import pe.edu.tecsup.evaluacionfinal.model.Led;

public class LedsFragment extends Fragment {

    private ImageView imgLed1;
    private ImageView imgLed2;
    private ImageView imgLed3;
    private Button btnLedOn1;
    private Button btnLedOn2;
    private Button btnLedOn3;
    private Button btnLedOff1;
    private Button btnLedOff2;
    private Button btnLedOff3;
    private Led led;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leds, container, false);

        imgLed1 = view.findViewById(R.id.imgLed1);
        imgLed2 = view.findViewById(R.id.imgLed2);
        imgLed3 = view.findViewById(R.id.imgLed3);
        btnLedOn1 = view.findViewById(R.id.btnLedOn1);
        btnLedOn2 = view.findViewById(R.id.btnLedOn2);
        btnLedOn3 = view.findViewById(R.id.btnLedOn3);
        btnLedOff1 = view.findViewById(R.id.btnLedOff1);
        btnLedOff2 = view.findViewById(R.id.btnLedOff2);
        btnLedOff3 = view.findViewById(R.id.btnLedOff3);

        // Conectar a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Utilizar la referencia "led"
        final DatabaseReference myRef = database.getReference("led");

        // Leer de la base de datos
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Capturando el valor de la referencia "saludo"
                led = dataSnapshot.getValue(Led.class);
                if (led.led1.equals("1")) {
                    imgLed1.setImageResource(R.drawable.led_on_blue);
                } else {
                    imgLed1.setImageResource(R.drawable.led_off);
                }

                if (led.led2.equals("1")) {
                    imgLed2.setImageResource(R.drawable.led_on_orange);
                } else {
                    imgLed2.setImageResource(R.drawable.led_off);
                }

                if (led.led3.equals("1")) {
                    imgLed3.setImageResource(R.drawable.led_on_yellow);
                } else {
                    imgLed3.setImageResource(R.drawable.led_off);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Enviar mensaje de error
            }
        });

        btnLedOn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led.led1 = "1";
                myRef.setValue(led);
            }
        });
        btnLedOff1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led.led1 = "0";
                myRef.setValue(led);
            }
        });
        btnLedOn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led.led2 = "1";
                myRef.setValue(led);
            }
        });
        btnLedOff2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led.led2 = "0";
                myRef.setValue(led);
            }
        });
        btnLedOn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led.led3 = "1";
                myRef.setValue(led);
            }
        });
        btnLedOff3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                led.led3 = "0";
                myRef.setValue(led);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}