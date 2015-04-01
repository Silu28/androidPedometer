package tanja.db.de.pedometer;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.media.MediaScannerConnection;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.ArrayList;


public class MainActivity extends Activity {
    static ArrayList<String> dataUnAcc;
    static ArrayList<String> dataCoAcc;
    static ArrayList<String> dataRot;
    static ArrayList<String> dataGyro;
    static String filenameUnAcc,filenameUnAcc2, filenameCoAcc2, filenameCoAcc, filenameRot,filenameRot2, filenameGyro,filenameGyro2;
    static TextView showData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showData = (TextView) findViewById(R.id.showDataTextView);
        ////////////////////////
        Button StepsRecord = (Button) findViewById(R.id.idStepsRecord);
        StepsRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dataCoAcc = new ArrayList<String>();
                dataRot = new ArrayList<String>();
                dataUnAcc = new ArrayList<String>();
                dataGyro = new ArrayList<String>();

                filenameCoAcc2 = "CoStepAcc.csv";
                filenameRot2 = "RoStep.csv";

                filenameUnAcc2 = "UnStepAcc.csv";
                filenameGyro2 = "GyrStep.csv";

                startService(new Intent(MainActivity.this,
                        SensorService.class));

            }
        });

        Button StepStop = (Button) findViewById(R.id.idStepsStop);
        StepStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                saveAll(filenameUnAcc2, dataUnAcc);
                saveAll(filenameCoAcc2, dataCoAcc);
                saveAll(filenameRot2, dataRot);
                saveAll(filenameGyro2, dataGyro);

                dataUnAcc.clear();
                dataCoAcc.clear();
                dataGyro.clear();
                dataRot.clear();
                stopService(new Intent(MainActivity.this,
                        SensorService.class));

            }

        });
        //////////////////////////

        Button record = (Button) findViewById(R.id.idRecord);
        record.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dataUnAcc = new ArrayList<String>();
                dataCoAcc = new ArrayList<String>();
                dataRot = new ArrayList<String>();
                dataGyro = new ArrayList<String>();

                filenameUnAcc = (""+((EditText)findViewById(R.id.idNameEingabe)).getText().toString().trim()+"UnAcc"+".csv");
                filenameCoAcc = (""+ ((EditText)findViewById(R.id.idNameEingabe)).getText().toString().trim()+"CoAcc"+".csv");
                filenameRot = (""+ ((EditText)findViewById(R.id.idNameEingabe)).getText().toString().trim()+"Ro"+".csv");
                filenameGyro = (""+ ((EditText)findViewById(R.id.idNameEingabe)).getText().toString().trim()+"Gy"+".csv");
                startService(new Intent(MainActivity.this,
                        SensorService.class));

            }
        });

        Button stop = (Button) findViewById(R.id.idStop);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveAll(filenameUnAcc, dataUnAcc);
                saveAll(filenameCoAcc, dataCoAcc);
                saveAll(filenameRot, dataRot);
                saveAll(filenameGyro, dataGyro);

                dataUnAcc.clear();
                dataCoAcc.clear();
                dataGyro.clear();
                dataRot.clear();
                stopService(new Intent(MainActivity.this,
                        SensorService.class));

            }

        });
    }

    public void saveAll(String name, ArrayList<String> data){
        try
        {
            File traceFile = new File(((Context)this).getExternalFilesDir(null), name); // Creates a trace file in the primary external storage space of the current application.
            if (!traceFile.exists()){        // If the file does not exists, it is created.
                traceFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/)); // Adds a line to the trace file
            for (String t: data){
                writer.write(t);
                writer.newLine();
            }
            writer.close();
            MediaScannerConnection.scanFile((Context) (this),
                    new String[]{traceFile.toString()},
                    null,
                    null);
        }
        catch (IOException e)
        {
            Log.e("ERROR", "saveAll Error");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}