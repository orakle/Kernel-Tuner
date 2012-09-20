package rs.pedjaapps.KernelTuner;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import rs.pedjaapps.KernelTuner.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

public class gpu extends Activity{

	public String gpu2dcurent;
	public String gpu3dcurent ;
	public String gpu2dmax;
	public String gpu3dmax;
	public String selected2d;

	public String selected3d;
	
	public int new3d;
	public int new2d;
	public String[] gpu2d = {"160", "200", "228", "266"};
	public String[] gpu3d = {"200", "228", "266", "300", "320"};
	
	private ProgressDialog pd = null;
	private Object data = null;
	public SharedPreferences preferences;
	


private class changegpu extends AsyncTask<String, Void, Object> {
	
	
	protected Object doInBackground(String... args) {
         Log.i("MyApp", "Background thread starting");



         Process localProcess;
    		try {
				localProcess = Runtime.getRuntime().exec("su");
			
    		DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
           localDataOutputStream.writeBytes("chmod 777 /sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk\n");
           //localDataOutputStream.writeBytes("chmod 777 /sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk\n");
           if(selected3d.equals("200")){
				new3d=200000000;
			}
			else if(selected3d.equals("228")){
				new3d=228571000;
			}
			else if(selected3d.equals("266")){
				new3d=266667000;
			}
			else if(selected3d.equals("300")){
				new3d=300000000;
			}
			else if(selected3d.equals("320")){
			new3d=320000000;
			}
         
        	   localDataOutputStream.writeBytes("echo " + new3d + " > /sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk\n");
         
           localDataOutputStream.writeBytes("chmod 777 /sys/devices/platform/kgsl-2d1.1/kgsl/kgsl-2d1/max_gpuclk\n");
          localDataOutputStream.writeBytes("chmod 777 /sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk\n");
           if(selected2d.equals("160")){
				new2d=160000000;
			System.out.println("new clock = " + new2d);
			}
          else if(selected2d.equals("200")){
				new2d=200000000;
				System.out.println("new clock = " +new2d);
			}
			else if(selected2d.equals("228")){
				new2d=228571000;
				System.out.println("new clock = " +new2d);
			}
			else if(selected2d.equals("266")){
				new2d=266667000;
				System.out.println("new clock = " +new2d);
			}
			
			
        
         	localDataOutputStream.writeBytes("echo " + new2d + " > /sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk\n");
         	localDataOutputStream.writeBytes("echo " + new2d + " > /sys/devices/platform/kgsl-2d1.1/kgsl/kgsl-2d1/max_gpuclk\n");   
            
        
         
           
           localDataOutputStream.writeBytes("exit\n");
           localDataOutputStream.flush();
           localDataOutputStream.close();
           localProcess.waitFor();
           localProcess.destroy();
    		} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


return "";
    }

    protected void onPostExecute(Object result) {
    	preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = preferences.edit();
	  	    editor.putString("gpu3d", selected3d);
	  	    editor.putString("gpu2d", selected2d);
	  	  // value to store
	  	    editor.commit();
        // Pass the result data back to the main activity
    	
        gpu.this.data = result;

        
           gpu.this.pd.dismiss();
           gpu.this.finish();
          
    }

	}
	
    	
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.gputest);
	readgpu2dcurent();
	
	readgpu3dcurent();
	readgpu2dmax();
	
	readgpu3dmax();
	
	TextView tv1 = (TextView)findViewById(R.id.textView5);
	TextView tv2 = (TextView)findViewById(R.id.textView7);
	
	tv1.setText(gpu3dcurent.substring(0, gpu3dcurent.length()-6)+"Mhz");
	tv2.setText(gpu2dcurent.substring(0, gpu2dcurent.length()-6)+"Mhz");

	
	//setprogress2d();
	//setprogress3d();
	
	Button apply = (Button)findViewById(R.id.button2);
	
	apply.setOnClickListener(new OnClickListener(){
		
		
		public void onClick(View v) {
			
			gpu.this.pd = ProgressDialog.show(gpu.this, "Working..", "Applying settings...", true, false);
			
			new changegpu().execute();
			
		    
		    
		}
	});
	
Button cancel = (Button)findViewById(R.id.button1);
	
	cancel.setOnClickListener(new OnClickListener(){
		
		
		public void onClick(View v) {
			
			gpu.this.finish();
		   
		    
		}
	});
	
	
	
    
}
	
	public void createSpinner2D(){
		
		
		final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, gpu2d);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		spinner.setAdapter(spinnerArrayAdapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	selected2d = parent.getItemAtPosition(pos).toString();
		    	
		    }

		    
		    public void onNothingSelected(AdapterView<?> parent) {
		        //do nothing
		    }
		});

		ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

		int spinnerPosition = myAdap.getPosition(gpu2dmax.substring(0, gpu2dmax.length()-6));

		//set the default according to value
		spinner.setSelection(spinnerPosition);
		
	}
	


public void createSpinner3D(){
	
	
	final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, gpu3d);
	spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
	spinner.setAdapter(spinnerArrayAdapter);
	
	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	selected3d = parent.getItemAtPosition(pos).toString();
	    	
	    }

	    
	    public void onNothingSelected(AdapterView<?> parent) {
	        //do nothing
	    }
	});

	ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

	int spinnerPosition = myAdap.getPosition(gpu3dmax.substring(0, gpu3dmax.length()-6));

	//set the default according to value
	spinner.setSelection(spinnerPosition);
	
}
    	

    	public void readgpu3dcurent(){
    		try {
     			
     			File myFile = new File("/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk");
     			FileInputStream fIn = new FileInputStream(myFile);

     			BufferedReader myReader = new BufferedReader(
     					new InputStreamReader(fIn));
     			String aDataRow = "";
     			String aBuffer = "";
     			while ((aDataRow = myReader.readLine()) != null) {
     				aBuffer += aDataRow + "\n";
     			}

     			gpu3dcurent = aBuffer.trim();
      		
     			myReader.close();
     			

     			
     		} catch (Exception e) {
     			Toast.makeText(getBaseContext(), e.getMessage(),
    					Toast.LENGTH_SHORT).show();
     			
     		}
    	}
    	
    	public void readgpu2dcurent(){
    	     try {
      			
      			File myFile = new File("/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk");
      			FileInputStream fIn = new FileInputStream(myFile);
      			BufferedReader myReader = new BufferedReader(
      					new InputStreamReader(fIn));
      			String aDataRow = "";
      			String aBuffer = "";
      			while ((aDataRow = myReader.readLine()) != null) {
      				aBuffer += aDataRow + "\n";
      			}

      			gpu2dcurent = aBuffer.trim();
      			
      			myReader.close();
      			
      			

      			
      		} catch (Exception e) {
      			
      		}
    	}
    	     
    	     
    	     public void readgpu3dmax(){
    	    		try {
    	     			
    	     			File myFile = new File("/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk");
    	     			FileInputStream fIn = new FileInputStream(myFile);

    	     			BufferedReader myReader = new BufferedReader(
    	     					new InputStreamReader(fIn));
    	     			String aDataRow = "";
    	     			String aBuffer = "";
    	     			while ((aDataRow = myReader.readLine()) != null) {
    	     				aBuffer += aDataRow + "\n";
    	     			}

    	     			gpu3dmax = aBuffer.trim();
    	     			Log.d("max gpu 3d clock",gpu3dmax);
    	      			
    	     			
    	     			createSpinner3D();
    	     			myReader.close();
    	     			

    	     			
    	     		} catch (Exception e) {
    	     			Log.e("max gpu 3d clock","not found");
    	     			
    	     		}
    	    	}
    	    	
    	    	public void readgpu2dmax(){
    	    	     try {
    	      			
    	      			File myFile = new File("/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk");
    	      			FileInputStream fIn = new FileInputStream(myFile);
    	      			BufferedReader myReader = new BufferedReader(
    	      					new InputStreamReader(fIn));
    	      			String aDataRow = "";
    	      			String aBuffer = "";
    	      			while ((aDataRow = myReader.readLine()) != null) {
    	      				aBuffer += aDataRow + "\n";
    	      			}

    	      			gpu2dmax = aBuffer.trim();
    	      			
    	      			createSpinner2D();
    	     			
    	      			myReader.close();
    	      			
    	      			

    	      			
    	      		} catch (Exception e) {
    	      			
    	      		}
    	    	}
    	    	     
    	    	     
    	
   
	
  
    

}
