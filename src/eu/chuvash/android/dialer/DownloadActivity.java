package eu.chuvash.android.dialer;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import eu.chuvash.android.dialer.util.FILE;
import eu.chuvash.android.dialer.util.ZIP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class DownloadActivity extends Activity {
	private static final String TAG = "DownloadActivity";
	private String downloadPageUrl = "";
	private String destination = "";
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		pd = ProgressDialog.show(this, "",
				getString(R.string.download_page_progress_message));
		initPaths();
		initDownloadWebView();
	}

	public void initPaths() {
		try {
			downloadPageUrl = getIntent().getExtras().getString("url");
			destination = getIntent().getExtras().getString("destination");
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			downloadPageUrl = "file:///android_asset/error.html";
		}
	}

	private void initDownloadWebView() {
		WebView dlw = (WebView) findViewById(R.id.download_webview);
		// should be checked if there is Internet access
		// and loading is successful
		dlw.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// check if in the same domain
				if (url.contains(downloadPageUrl.substring(0, downloadPageUrl.lastIndexOf("/")))) {
					view.loadUrl(url); // important!
				} else {
					Toast.makeText(DownloadActivity.this,
							getString(R.string.download_only_audio_toast),
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}

			// http://www.chrisdanielson.com/tag/webview/
			public void onPageFinished(WebView view, String url) {
				if (pd != null && pd.isShowing()) {
					pd.dismiss();
				}
			}
		});
		dlw.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String downloadUrl, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				new DownloadActivity.DownloadSoundTask().execute(downloadUrl);
			}
		});
		dlw.loadUrl(downloadPageUrl);
	}

	private class DownloadSoundTask extends AsyncTask<String, Integer, Integer> {
		private ProgressDialog pd = new ProgressDialog(DownloadActivity.this);
		private String path = "";

		@Override
		protected void onPreExecute() {
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage(getString(R.string.download_new_songs_label));
			pd.setCancelable(false);
			pd.setMax(100);
			pd.setProgress(0);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... urls) {
			int percent = 0;
			int offset = 0;
			String downloadUrl = urls[0];

			String fileName = downloadUrl.substring(downloadUrl
					.lastIndexOf("/") + 1);
			// String url1 =
			// "http://www.programvaruteknik.nu/dt031g/dialpad/sounds/kari_no.dps";
			// String fileName = url1.substring(url1.lastIndexOf("/")+1);
			path = destination + fileName;
			// TODO bygg in kontroll om det finns redan en sådan fil/katalog
			// AlertDialog?
			// http://stackoverflow.com/questions/1955876/downloading-files-on-android
			// http://www.javabeat.net/tips/36-file-upload-and-download-using-java.html
			// http://www.java2s.com/Tutorial/Java/0320__Network/SavebinaryfilefromURL.htm
			URL requestURL = null;
			URLConnection con = null;
			InputStream in = null;
			FileOutputStream out = null;
			try {
				requestURL = new URL(downloadUrl);
				con = requestURL.openConnection();
				in = new BufferedInputStream(con.getInputStream());
				int contentLength = con.getContentLength();
				byte[] data = new byte[contentLength];
				offset = 0;
				int bytesRead = 0;
				while (offset < contentLength) {
					bytesRead = in.read(data, offset, data.length - offset);
					offset += bytesRead;
					percent = offset * 100 / contentLength;
					publishProgress(percent);
				}
				out = new FileOutputStream(path);
				out.write(data);
				out.flush(); // close is in another try/catch, see below
			} catch (MalformedURLException e) {
				Log.e(TAG, "Fel MalformedURLException i DownloadTask: "
						+ e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO notificate user if failed
				Log.e(TAG, "Fel IOException i DownloadTask: " + e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						Log.e(TAG,
								"Kan inte stänga inströmmen i DownloadTask: "
										+ e.toString());
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						Log.e(TAG,
								"Kan inte stänga utströmmen i DownloadTask: "
										+ e.toString());
					}
				}
			}
			return offset;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			pd.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (this.pd != null && this.pd.isShowing()) {
				pd.dismiss();
			}
			if (!path.equals("") && !destination.equals("")) {
				new DecompressTask().execute(path, destination);
			}
		}
	}

	private class DecompressTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog pd = new ProgressDialog(DownloadActivity.this);

		@Override
		protected void onPreExecute() {
			pd.setMessage(getString(R.string.decompressing));
			pd.show();
			super.onPreExecute();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 * 
		 * @param path to zip file and path to directory to unpack
		 */
		@Override
		protected Void doInBackground(String... params) {
			String filePath = params[0];
			String directoryPathToUnzip = params[1];
			ZIP.decompress(filePath, directoryPathToUnzip);
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (this.pd != null && this.pd.isShowing()) {
				pd.dismiss();
			}
			new CleanTask().execute(destination);
		}
	}

	private class CleanTask extends AsyncTask<String, Void, Void> {
		// TODO kanske en progress dialog
		// vad händer om man trycker på "Back"
		@Override
		protected Void doInBackground(String... params) {
			// http://www.exampledepot.com/egs/java.io/DeleteDir.html
			String directoryToClean = params[0];
			FILE.cleanDirectory(directoryToClean, "dps");
			return null;
		}
	}
}
