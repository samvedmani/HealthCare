package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    Intent intent1;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch ((menuItem.getItemId())){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_prescription:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PrescriptionFragment()).commit();
                break;
            case R.id.nav_appoint:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AppointmentFragment()).commit();
                break;
            case R.id.nav_track:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TrackerFragment()).commit();
                break;
            case R.id.nav_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://drive.google.com/file/d/1iOyLF4upfepkEGm8CSZqaYSIV0mNQkXi/view?usp=sharing");
                startActivity(Intent.createChooser(intent, "SHARE VIA"));
//                sharedApplication();
                break;
            case R.id.nav_help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void sharedApplication() {
//        ApplicationInfo app = getApplicationContext().getApplicationInfo();
//        String filePath = app.sourceDir;
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//
//        // MIME of .apk is "application/vnd.android.package-archive".
//        // but Bluetooth does not accept this. Let's use "*/*" instead.
//        intent.setType("*/*");
//
//        // Append file and send Intent
//        File originalApk = new File(filePath);
//
//        try {
//            //Make new directory in new location
//            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
//            //If directory doesn't exists create new
//            if (!tempFile.isDirectory())
//                if (!tempFile.mkdirs())
//                    return;
//            //Get application's name and convert to lowercase
//            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
//            //If file doesn't exists create new
//            if (!tempFile.exists()) {
//                if (!tempFile.createNewFile()) {
//                    return;
//                }
//            }
//            //Copy file to new location
//            InputStream in = new FileInputStream(originalApk);
//            OutputStream out = new FileOutputStream(tempFile);
//
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            in.close();
//            out.close();
//            System.out.println("File copied.");
//            //Open share dialog
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
//            startActivity(Intent.createChooser(intent, "Share app via"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(backPressedTime+ 2000> System.currentTimeMillis()){
                super.onBackPressed();
                moveTaskToBack(true);
                return;
            } else {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            }

            backPressedTime=System.currentTimeMillis();
        }
    }

}
