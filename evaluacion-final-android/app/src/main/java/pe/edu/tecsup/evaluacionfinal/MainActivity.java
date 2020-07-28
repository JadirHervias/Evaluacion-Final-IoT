package pe.edu.tecsup.evaluacionfinal;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import pe.edu.tecsup.evaluacionfinal.fragments.LedsFragment;
import pe.edu.tecsup.evaluacionfinal.fragments.MeasuresFragment;

public class MainActivity extends AppCompatActivity {

    MeasuresFragment measuresFragment;
    LedsFragment ledsFragment;
    FragmentManager fragmentManager;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("ON CREATE main");

        // Obtenemos el refreshedToken (instanceid)
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new
          OnSuccessListener<InstanceIdResult>() {
              @Override
              public void onSuccess(InstanceIdResult instanceIdResult) {
                  String newToken = instanceIdResult.getToken();
                  Log.e("newToken",newToken);
              }
          });

        // Nos suscribimos al tópico 'ALL'
        FirebaseMessaging.getInstance().subscribeToTopic("ALL");

        fragmentManager = getSupportFragmentManager();

        measuresFragment = new MeasuresFragment();
        ledsFragment = new LedsFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottonNavMethod);
        fragmentManager.beginTransaction().add(R.id.container, measuresFragment).commit();
        currentFragment = fragmentManager.findFragmentById(R.id.measures_layout);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottonNavMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    item.setChecked(true);
                    fragmentTransaction.replace(R.id.container, measuresFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                case R.id.navigation_dashboard:
                    item.setChecked(true);
                    fragmentTransaction.replace(R.id.container, ledsFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
            }
            return false;
        }
    };

}