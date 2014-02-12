package in.co.nebulax.magpie;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.splash);
			
			Thread timer = new Thread() {
				public void run() {
					try {
						sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						
							Intent openStartingPoint = new Intent(Splash.this , MainActivity.class);
							openStartingPoint.putExtra("StringClass", "Splash");
							startActivity(openStartingPoint);
							
						
					}
				}
			};
			timer.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}




