package app.arsybai.mlbb.anime.intro;

import android.Manifest;
import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.cyberalpha.darkIOS.*;
import com.example.iosprogressbarforandroid.*;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.sdsmdg.tastytoast.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class MainActivity extends AppCompatActivity {
	
	private String _ad_unit_id;
	
	private String url = "";
	private String filename = "";
	private String path = "";
	private String path1 = "";
	private String result = "";
	private String filePath = "";
	private double sumCount = 0;
	private double size = 0;
	private IOSProgressHUD peli;
	private double meki = 0;
	
	private ArrayList<HashMap<String, Object>> intros = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private GridView gridview1;
	private AdView adview1;
	private Button submitIntro;
	private Button Reset;
	
	private Intent intent = new Intent();
	private SharedPreferences sp;
	private RequestNetwork req;
	private RequestNetwork.RequestListener _req_request_listener;
	private InterstitialAd ads;
	private InterstitialAdLoadCallback _ads_interstitial_ad_load_callback;
	private FullScreenContentCallback _ads_full_screen_content_callback;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		
		MobileAds.initialize(this);
		_ad_unit_id = "ca-app-pub-2821623139709959/9279445520";
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		gridview1 = findViewById(R.id.gridview1);
		adview1 = findViewById(R.id.adview1);
		submitIntro = findViewById(R.id.submitIntro);
		Reset = findViewById(R.id.Reset);
		sp = getSharedPreferences("PERMISSION", Activity.MODE_PRIVATE);
		req = new RequestNetwork(this);
		
		gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				intent.putExtra("VID_URI", intros.get((int)_position).get("uri").toString());
				intent.setClass(getApplicationContext(), PreviewActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		submitIntro.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				iOSDarkBuilder dialog = new iOSDarkBuilder(MainActivity.this);
				dialog
				.setTitle("Submit Intro")
				.setSubtitle("You're about to send the intro for this app.\nAfter you click continue you will redirect to email and send your intro.\n\nPlease note that intro can't be more than 15seconds duration and AMV/ANIME video only allowed.") 	
				.setBoldPositiveLabel(false)
					.setCancelable(false)
				.setPositiveListener("Continue",new iOSDarkClickListener() 
				{ 	 @Override 	 public void onClick(iOSDark dialog) { 		
						dialog.dismiss(); 
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("mailto:me@arsybai.com?subject=Contribute MLBB Intro"));
						startActivity(intent);	 
					}
				})	
				.setNegativeListener("Cancel", new iOSDarkClickListener() 
				{ 	 
					@Override 	 public void onClick(iOSDark dialog) { 		
						dialog.dismiss(); 
					}
				})
				.build().show();
			}
		});
		
		Reset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				intent.setClass(getApplicationContext(), PickintroActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		_req_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				for(int _repeat20 = 0; _repeat20 < (int)(Integer.parseInt(_response)); _repeat20++) {
					{
						HashMap<String, Object> _item = new HashMap<>();
						_item.put("uri", "https://preview.arsybai.app/mlbbanimeintro/".concat(String.valueOf((long)(meki + 1)).concat(".mp4")));
						intros.add((int)meki, _item);
					}
					
					meki++;
				}
				gridview1.setAdapter(new Gridview1Adapter(intros));
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				TastyToast.makeText(getApplicationContext(), "Network Error", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
			}
		};
		
		_ads_interstitial_ad_load_callback = new InterstitialAdLoadCallback() {
			@Override
			public void onAdLoaded(InterstitialAd _param1) {
				ads = _param1;
				if (ads != null) {
					ads.show(MainActivity.this);
				} else {
					SketchwareUtil.showMessage(getApplicationContext(), "Error: InterstitialAd ads hasn't been loaded yet!");
				}
			}
			
			@Override
			public void onAdFailedToLoad(LoadAdError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		
		_ads_full_screen_content_callback = new FullScreenContentCallback() {
			@Override
			public void onAdDismissedFullScreenContent() {
				
			}
			
			@Override
			public void onAdFailedToShowFullScreenContent(AdError _adError) {
				final int _errorCode = _adError.getCode();
				final String _errorMessage = _adError.getMessage();
				
			}
			
			@Override
			public void onAdShowedFullScreenContent() {
				
			}
		};
	}
	
	private void initializeLogic() {
		
		submitIntro.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)10, 0xFF2979FF));
		Reset.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)10, 0xFFFF1744));
		if (Build.VERSION.SDK_INT>29) {
				FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext()).concat(""));
		}
		else {
				FileUtil.makeDir(FileUtil.getExternalStorageDir().concat(""));
		}
		filePath = "/storage/emulated/0/Android/data/com.mobile.legends/files/dragon2017/assets/Audio/android/";
		if (getIntent().hasExtra("INJECT")) {
			new DownloadTask().execute(getIntent().getStringExtra("INJECT"));
			{
				AdRequest adRequest = new AdRequest.Builder().build();
				InterstitialAd.load(MainActivity.this, _ad_unit_id, adRequest, _ads_interstitial_ad_load_callback);
			}
		}
		if (getIntent().hasExtra("IS_PICKED")) {
			if (checkPermission(pathToRealUri("/storage/emulated/0/Android/data/com.mobile.legends/files/dragon2017/assets/Audio/android/"))) {
				path = FileUtil.getExternalStorageDir().concat("/Download/mlbbAnimeIntro/".concat("splash.mp4"));
				path1 = FileUtil.getExternalStorageDir().concat("/Android/data/com.mobile.legends/files/dragon2017/assets/Audio/android/");
				if (copy(new java.io.File(path), path1, getApplicationContext())) {
					TastyToast.makeText(getApplicationContext(), "Success", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
				}
				else {
					TastyToast.makeText(getApplicationContext(), "Failed to change Intro", TastyToast.LENGTH_LONG, TastyToast.ERROR);
				}
			}
			else {
				iOSDarkBuilder dialog = new iOSDarkBuilder(MainActivity.this);
				dialog
				.setTitle("Request Permission ")
				.setSubtitle("Allow this app to use MLBB folder") 	
				.setBoldPositiveLabel(false)
					.setCancelable(false)
				.setPositiveListener("Allow",new iOSDarkClickListener() 
				{ 	 @Override 	 public void onClick(iOSDark dialog) { 		
						askPermission(pathToUri(filePath));
						dialog.dismiss(); 	 
					}
				})	
				.build().show();
			}
		}
		if (!FileUtil.isDirectory(FileUtil.getExternalStorageDir().concat("/Download/mlbbAnimeIntro"))) {
			FileUtil.makeDir(FileUtil.getExternalStorageDir().concat("/Download/mlbbAnimeIntro"));
		}
		if (!sp.contains("PATH_PERMISSION")) {
			iOSDarkBuilder dialog = new iOSDarkBuilder(MainActivity.this);
			dialog
			.setTitle("Request Permission ")
			.setSubtitle("Allow this app to use MLBB folder") 	
			.setBoldPositiveLabel(false)
				.setCancelable(false)
			.setPositiveListener("Allow",new iOSDarkClickListener() 
			{ 	 @Override 	 public void onClick(iOSDark dialog) { 		
					askPermission(pathToUri(filePath));
					dialog.dismiss(); 	 
				}
			})	
			.build().show();
		}
		if (SketchwareUtil.isConnected(getApplicationContext())) {
			req.startRequestNetwork(RequestNetworkController.GET, "https://preview.arsybai.app/mlbbanimeintro/intro.txt", "rr", _req_request_listener);
		}
		else {
			TastyToast.makeText(getApplicationContext(), "No Internet", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
		}
		{
			AdRequest adRequest = new AdRequest.Builder().build();
			adview1.loadAd(adRequest);
		}
	}
	
	@Override
	public void onBackPressed() {
		iOSDarkBuilder dialog = new iOSDarkBuilder(MainActivity.this);
		dialog
		.setTitle("Exit")
		.setSubtitle("Are you sure want to exit?") 	
		.setBoldPositiveLabel(false)
			.setCancelable(false)
		.setPositiveListener("Yes",new iOSDarkClickListener() 
		{ 	 @Override 	 public void onClick(iOSDark dialog) { 		
				dialog.dismiss(); 
				finish();	 
			}
		})	
		.setNegativeListener("No", new iOSDarkClickListener() 
		{ 	 
			@Override 	 public void onClick(iOSDark dialog) { 		
				dialog.dismiss(); 
			}
		})
		.build().show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (adview1 != null) {
			adview1.destroy();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (adview1 != null) {
			adview1.pause();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (adview1 != null) {
			adview1.resume();
		}
	}
	public void _kontol() {
	}
	
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		 @Override
		protected void onPreExecute() {
			peli = IOSProgressHUD.create(MainActivity.this)
			.setStyle(IOSProgressHUD.Style.ANNULAR_DETERMINATE)
			.setLabel("Downloading")
			.setCancellable(true)
			.setMaxProgress(100)
			.show();}
		protected String doInBackground(String... address) {
			try {
				filename= URLUtil.guessFileName(address[0], null, null);
				int resCode = -1;
				java.io.InputStream in = null;
				java.net.URL url = new java.net.URL(address[0]);
				java.net.URLConnection urlConn = url.openConnection();
				if (!(urlConn instanceof java.net.HttpURLConnection)) {
					throw new java.io.IOException("URL is not an Http URL"); }
				java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) urlConn; httpConn.setAllowUserInteraction(false); httpConn.setInstanceFollowRedirects(true); httpConn.setRequestMethod("GET"); httpConn.connect();
				resCode = httpConn.getResponseCode();
				if (resCode == java.net.HttpURLConnection.HTTP_OK) {
					in = httpConn.getInputStream();
					size = httpConn.getContentLength();
					
				} else { result = "There was an error"; }
				/**/
				path = FileUtil.getExternalStorageDir().concat("/Download/mlbbAnimeIntro/".concat("splash.mp4"));
				path1 = FileUtil.getExternalStorageDir().concat("/Android/data/com.mobile.legends/files/dragon2017/assets/Audio/android/");
				java.io.File file = new java.io.File(path);
				
				java.io.OutputStream output = new java.io.FileOutputStream(file);
				try {
					int bytesRead;
					sumCount = 0;
					byte[] buffer = new byte[1024];
					while ((bytesRead = in.read(buffer)) != -1) {
						output.write(buffer, 0, bytesRead);
						sumCount += bytesRead;
						if (size > 0) {
							publishProgress((int)Math.round(sumCount*100 / size));
						}
					}
				} finally {
					output.close();
				}
				result ="";
				in.close();
			} catch (java.net.MalformedURLException e) {
				result = e.getMessage();
			} catch (java.io.IOException e) {
				result = e.getMessage();
			} catch (Exception e) {
				result = e.toString();
			}
			return result;
			
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			peli.setProgress(values[values.length - 1]);
			peli.setDetailsLabel(String.valueOf((long)(values[values.length - 1])).concat("% Downloaded "));
		}
		@Override
		protected void onPostExecute(String s){
			
			peli.dismiss();
			if (checkPermission(pathToRealUri(filePath))) {
				if (copy(new java.io.File(path), path1, getApplicationContext())) {
					if (FileUtil.isFile(path)) {
						FileUtil.deleteFile(path);
						TastyToast.makeText(getApplicationContext(), "Success", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
					}
				}
				else {
					if (FileUtil.isFile(path)) {
						FileUtil.deleteFile(path);
						TastyToast.makeText(getApplicationContext(), "Failed", TastyToast.LENGTH_LONG, TastyToast.ERROR);
					}
				}
			}
			else {
				iOSDarkBuilder dialog = new iOSDarkBuilder(MainActivity.this);
				dialog
				.setTitle("Permission Required")
				.setSubtitle("Allow this app to access MLBB folder") 	
				.setBoldPositiveLabel(false)
					.setCancelable(false)
				.setPositiveListener("Allow",new iOSDarkClickListener() 
				{ 	 @Override 	 public void onClick(iOSDark dialog) { 		
						askPermission(pathToUri(filePath));
						dialog.dismiss(); 	 
					}
				})	
				.build().show();
			}
		}
	}
	/*
Code Edited by Hichem Soft
youtube channel : Hichem Soft
support me if you like my work
*/
	@Override
	    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		  super.onActivityResult(_requestCode, _resultCode, _data);
		            
		if (_requestCode == new_folder){
			    if (_resultCode == Activity.RESULT_OK) {
				            if (_data != null) {
					              final Uri uri2 = _data.getData();
					if (Uri.decode(uri2.toString()).endsWith(":")) {
						SketchwareUtil.showMessage(getApplicationContext(), "error");
						askPermission(uri2.toString());
					}
					else {
						final int takeFlags = i.getFlags()
						            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
						            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
						// Check for the freshest data.
						getContentResolver().takePersistableUriPermission(uri2, takeFlags);
						
						
						TastyToast.makeText(getApplicationContext(), "Access Granted", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
						sp.edit().putString("PATH_PERMISSION", "true").commit();
						
						
					}
					
					       } else {
					        
					   }
				       } else {
				      
				TastyToast.makeText(getApplicationContext(), "You decline the permission", TastyToast.LENGTH_LONG, TastyToast.ERROR);
				 
				   }
		}
		
		
		if (_requestCode == 2000) {
				      if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
						        if (Environment.isExternalStorageManager()) {
								          
								        } else {
								
								        }
						      }
				    
		}
		
		
		
		       
		
	}
	
	// solve android 11 sdcard permissions
	
	
	 public void RequestPermission_Dialog() {
		    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				      try {
						        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
						        intent.addCategory("android.intent.category.DEFAULT");
						        intent.setData(Uri.parse(String.format("package: ", new Object[]{getApplicationContext().getPackageName()})));
						        startActivityForResult(intent, 2000);
						      } catch (Exception e) {
						        Intent obj = new Intent();
						        obj.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
						        startActivityForResult(obj, 2000);
						      }
				    } else {
				      androidx.core.app.ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
				    }
		  }
	
	  public boolean permission() {
		    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) { // R is Android 11
				      return Environment.isExternalStorageManager();
				    } else {
				      int write = androidx.core.content.ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
				      int read = androidx.core.content.ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
				
				      return write == android.content.pm.PackageManager.PERMISSION_GRANTED
				          && read == android.content.pm.PackageManager.PERMISSION_GRANTED;
				    }
	} 
	
	// ask permissions request
	
	    
	    public void askPermission(final String _uri) {
			
		
			i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |  Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
					
			i.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);				    i.putExtra(android.provider.DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(_uri));
						        startActivityForResult(i, new_folder);
		}
	
	// check permissions of path if accepted 
	
	
	public boolean checkPermission(final String _uri) {
				Uri muri = Uri.parse(_uri);
				    dFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(getApplicationContext(), muri);
				                    
				if (dFile.canRead() && dFile.canWrite()) {
						return true ;
				}
				return false ;
		}
	
	// simple path to UriTree path
	
	
	public String pathToRealUri( String _path) {
				uriFor1 = "content://com.android.externalstorage.documents/tree/primary%3A";
		
		if ( _path.endsWith("/")) {
			_path = _path.substring(0, _path.length()-1);
		}
		
		
				if (_path.contains("/sdcard/")) {
						uriFor2 = _path.replace("/sdcard/", "").replace("/", "%2F");
						
						if (uriFor2.substring(uriFor2.length()-1, uriFor2.length()).equals("/")) {
								
								uriFor2 = uriFor1.substring(0, uriFor1.length()-1);
								
						}
						
				}
				else {
						if (_path.contains("/storage/") && _path.contains("/emulated/")) {
								uriFor2 = _path.replace("/storage/emulated/0/", "").replace("/", "%2F");
								
								if (uriFor2.substring(uriFor2.length()-1, uriFor2.length()).equals("/")) {
										
										uriFor2 = uriFor1.substring(0, uriFor1.length()-1);
										
								}	
								
						}
						else {
								
						}
				}
				return uriFor1 = uriFor1 + uriFor2;
		}
	
	
	// simple path to UriTree path 2
	
	public String pathToUri( String _path) {
				uriFor1 = "content://com.android.externalstorage.documents/tree/primary%3AAndroid/document/primary%3A";
		
		if ( _path.endsWith("/")) {
			_path = _path.substring(0, _path.length()-1);
		}
		
				if (_path.contains("/sdcard/")) {
						uriFor2 = _path.replace("/sdcard/", "").replace("/", "%2F");
						
						if (uriFor2.substring(uriFor2.length()-1, uriFor2.length()).equals("/")) {
								
								uriFor2 = uriFor1.substring(0, uriFor1.length()-1);
								
						}
						
						
				}
				else {
						if (_path.contains("/storage/") && _path.contains("/emulated/")) {
								uriFor2 = _path.replace("/storage/emulated/0/", "").replace("/", "%2F");
								
								if (uriFor2.substring(uriFor2.length()-1, uriFor2.length()).equals("/")) {
										
										uriFor2 = uriFor1.substring(0, uriFor1.length()-1);
										
								}
								
						}
						else {
								
						}
				}
				return uriFor1 = uriFor1 + uriFor2;
		}
	
	// ccopy file from path to path
	
	private boolean copyAsset(final String assetFilename, final Uri targetUri) {
		  			try{
			  				int count;
			  				InputStream input = null;
					OutputStream output = null;
			  				
			  				ContentResolver content = getApplicationContext().getContentResolver();
						  
			            input = getApplicationContext().getAssets().open(assetFilename);
						
			            output = content.openOutputStream(targetUri);
			            
			            
			  				byte data[] = new byte[1024];
			  				while ((count = input.read(data))>0) {
				  					output.write(data, 0, count);
				  			}
			  				output.flush();
			  				output.close();
			  				input.close();
			  				
			  				SketchwareUtil.showMessage(getApplicationContext(), "success");
							 
			  		}catch(Exception e){
			  				
			  		FileUtil.writeFile("/sdcard/log.txt", "\n"+ "3   " +e.toString());		SketchwareUtil.showMessage(getApplicationContext(), e.toString());
							  return false;
			  		}
		
		return true;
	}
	
	
	
	
	
	private void copyAssetFolder(String  _folder, String _out ) {
		
		
		AssetManager assetManager = getAssets();
		int sizeList = 0;
		    String[] files = null;
		    try {
				        files = assetManager.list(_folder);
				
				    } catch (java.io.IOException e) {
				        
				    }
		final ArrayList<String> str = new ArrayList<>(Arrays.asList(files));
		
		
		
		int nn = 0;
		for(int _repeat12 = 0; _repeat12 < (int)(str.size()); _repeat12++) {
				
				try {
							Uri mUri = Uri.parse(pathToRealUri(_out));
							
							String fileName = str.get((int)nn);
					sizeList = str.size()-1;		
						
						androidx.documentfile.provider.DocumentFile dFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(getApplicationContext(), mUri);
							           Uri mUri2 = Uri.parse(mUri.toString()+ "%2" + fileName);
							          androidx.documentfile.provider.DocumentFile  dFile2 = androidx.documentfile.provider.DocumentFile.fromTreeUri(getApplicationContext(), mUri2);
							            
							  
							
							try {              
										
										androidx.documentfile.provider.DocumentFile file = dFile.findFile(fileName);
										   android.provider.DocumentsContract.deleteDocument(getApplicationContext().getContentResolver(), file.getUri());
										
								    android.provider.DocumentsContract.deleteDocument(getApplicationContext().getContentResolver(), mUri2);
										
										
							} catch (FileNotFoundException e) {
										                } catch (Exception e2) {
										                }
							
							
							
							dFile2 = dFile.createFile("*/*", fileName);
							            mUri = dFile2.getUri();
							        
							        
							        
							        if (copyAsset(_folder+"/"+fileName, mUri)) {
										    
						if (nn >= sizeList){				    SketchwareUtil.showMessage(getApplicationContext(), "️😎✔️");       
					}					        
						} else {
										            
										        
								SketchwareUtil.showMessage(getApplicationContext(), "😓❌");
								break;
						}
							
							                
							      } catch (Exception re){}      
				            
				nn++;
		}
		
	}
	
	
	public boolean copy(java.io.File copy, String directory, Context con) {
		     java.io.FileInputStream inStream = null;
		     java.io.OutputStream outStream = null;
		    androidx.documentfile.provider.DocumentFile  dir= androidx.documentfile.provider.DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(pathToRealUri(directory)));
		    
		 
		try {   
			  String fileN = Uri.parse(copy.getPath()).getLastPathSegment();  
			
			    androidx.documentfile.provider.DocumentFile file = dir.findFile(fileN);
			   android.provider.DocumentsContract.deleteDocument(getApplicationContext().getContentResolver(), file.getUri());
			    
			 } catch (Exception e){
			e.printStackTrace();
		}   
		    String mim = mime(copy.toURI().toString());
		    androidx.documentfile.provider.DocumentFile  copy1= dir.createFile(mim, copy.getName());
		    try {
			        inStream = new java.io.FileInputStream(copy);
			        outStream =
			                con.getContentResolver().openOutputStream(copy1.getUri());
			        byte[] buffer = new byte[16384];
			        int bytesRead;
			        while ((bytesRead = inStream.read(buffer)) != -1) {
				            outStream.write(buffer, 0, bytesRead);
				
				        }
			    } catch (java.io.FileNotFoundException e) {
			        e.printStackTrace();
			    } catch (java.io.IOException e) {
			        e.printStackTrace();
			    } finally {
			        try {
				
				            inStream.close();
				
				            outStream.close();
				
				
				            return true;
				
				
				        } catch (java.io.IOException e) {
				            e.printStackTrace();
				        }
			    }
		    return false;
	}
	
	
	
	public  String mime(String URI) {
		       String type = "";
		       String extention = android.webkit.MimeTypeMap.getFileExtensionFromUrl(URI);
		       if (extention != null) {
			           type = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
			       }
		       return type;
		   }
	
	
	
	
	
	private boolean fromStorage = false;
	  final static int REQUEST_CODE = 333;
	  final static  int OLD_REQUEST = 2000;
	  private SharedPreferences sha;
	private Intent i = new Intent();
		private  Uri muri;
		private String uriFor1 = "";
		private String uriFor2 = "";
		private  
		androidx.documentfile.provider.DocumentFile dFile;
		private double PermissionNumber;
		private  static final int new_folder = 43;
	{
	}
	
	public class Gridview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Gridview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.lll, null);
			}
			
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			
			Glide.with(getApplicationContext()).load(Uri.parse(_data.get((int)_position).get("uri").toString().replace(".mp4", ".png"))).into(imageview1);
			imageview1.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)10, Color.TRANSPARENT));
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}