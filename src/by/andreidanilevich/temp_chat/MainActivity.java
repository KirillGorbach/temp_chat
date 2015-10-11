package by.andreidanilevich.temp_chat;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	// ��� ������� (url ������������������� ���� �����)
	// �������� http://l29340eb.bget.ru
	String server_name = "http://l29340eb.bget.ru";

	Spinner spinner_author, spinner_client;
	String author, client;
	Button open_chat_btn, open_chat_reverce_btn, delete_server_chat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.i("chat", "+ MainActivity - ������ ����������");

		open_chat_btn = (Button) findViewById(R.id.open_chat_btn);
		open_chat_reverce_btn = (Button) findViewById(R.id.open_chat_reverce_btn);
		delete_server_chat = (Button) findViewById(R.id.delete_server_chat);

		// �������� FoneService
		this.startService(new Intent(this, FoneService.class));

		// �������� 2 ���������� ���� ��� ������ ������ � ���������� ���������
		// 5 ������� � 5 ������� ����
		// ��������� ����������
		spinner_author = (Spinner) findViewById(R.id.spinner_author);
		spinner_client = (Spinner) findViewById(R.id.spinner_client);

		spinner_author.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "����",
						"����", "����", "������", "������", "���", "����",
						"�����", "������", "������" }));
		spinner_client.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "����",
						"����", "����", "������", "������", "���", "����",
						"�����", "������", "������" }));
		spinner_client.setSelection(5);

		open_chat_btn.setText("������� ���: "
				+ spinner_author.getSelectedItem().toString() + " > "
				+ spinner_client.getSelectedItem().toString());
		open_chat_reverce_btn.setText("������� ���: "
				+ spinner_client.getSelectedItem().toString() + " > "
				+ spinner_author.getSelectedItem().toString());

		spinner_author
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View itemSelected, int selectedItemPosition,
							long selectedId) {

						author = spinner_author.getSelectedItem().toString();

						open_chat_btn.setText("������� ���: "
								+ spinner_author.getSelectedItem().toString()
								+ " > "
								+ spinner_client.getSelectedItem().toString());
						open_chat_reverce_btn.setText("������� ���: "
								+ spinner_client.getSelectedItem().toString()
								+ " > "
								+ spinner_author.getSelectedItem().toString());
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		spinner_client
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View itemSelected, int selectedItemPosition,
							long selectedId) {

						client = spinner_client.getSelectedItem().toString();

						open_chat_btn.setText("������� ���: "
								+ spinner_author.getSelectedItem().toString()
								+ " > "
								+ spinner_client.getSelectedItem().toString());
						open_chat_reverce_btn.setText("������� ���: "
								+ spinner_client.getSelectedItem().toString()
								+ " > "
								+ spinner_author.getSelectedItem().toString());
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
	}

	// ������� ��� � ��������� ������� � �����������
	public void open_chat(View v) {
		// ������� ��������
		if (author.equals(client)) {
			// ���� ����� � ���������� ���������
			// ��� �� ���������
			Toast.makeText(this, "author = client !", Toast.LENGTH_SHORT)
					.show();
		} else {
			// ������� ������ ��� author > client
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra("author", author);
			intent.putExtra("client", client);
			startActivity(intent);
		}
	}

	// ������� ��� � ��������� ������� � �����������, ������ ��������
	public void open_chat_reverce(View v) {
		// ������� ��������
		if (author.equals(client)) {
			// ���� ����� � ���������� ���������
			// ��� �� ���������
			Toast.makeText(this, "author = client !", Toast.LENGTH_SHORT)
					.show();
		} else {
			// ������� ������ ��� client > author
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra("author", client);
			intent.putExtra("client", author);
			startActivity(intent);
		}
	}

	// �������� ������ �� ������ � �������� ������� � ������
	public void delete_server_chats(View v) {

		Log.i("chat", "+ MainActivity - ������ �� �������� ���� � �������");

		delete_server_chat.setEnabled(false);
		delete_server_chat.setText("������ ���������. ��������...");

		DELETEfromChat delete_from_chat = new DELETEfromChat();
		delete_from_chat.execute();
	}

	// ������ ��������� ������� �����
	// � �������� ������� �����
	public void delete_local_chats(View v) {

		Log.i("chat", "+ MainActivity - �������� ���� � ����� ����������");

		SQLiteDatabase chatDBlocal;
		chatDBlocal = openOrCreateDatabase("chatDBlocal.db",
				Context.MODE_PRIVATE, null);
		chatDBlocal.execSQL("drop table chat");
		chatDBlocal
				.execSQL("CREATE TABLE IF NOT EXISTS chat (_id integer primary key autoincrement, author, client, data, text)");

		Toast.makeText(getApplicationContext(),
				"��� �� ���� ���������� ������!", Toast.LENGTH_SHORT).show();
	}

	// �������� ������ �� ������ � �������� ������� � ������
	// ���� �� ������� - ������� ����� �������
	// ���� �� ������� (�������� ��� ��������� ��� ������ ����������)
	// - ������� ���������
	private class DELETEfromChat extends AsyncTask<Void, Void, Integer> {

		Integer res;
		HttpURLConnection conn;

		protected Integer doInBackground(Void... params) {

			try {
				URL url = new URL(server_name + "/chat.php?action=delete");
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000); // ���� 10���
				conn.setRequestMethod("POST");
				conn.setRequestProperty("User-Agent", "Mozilla/5.0");
				conn.connect();
				res = conn.getResponseCode();
				Log.i("chat", "+ MainActivity - ����� ������� (200 = ��): "
						+ res.toString());

			} catch (Exception e) {
				Log.i("chat",
						"+ MainActivity - ����� ������� ������: "
								+ e.getMessage());
			} finally {
				conn.disconnect();
			}

			return res;
		}

		protected void onPostExecute(Integer result) {

			try {
				if (result == 200) {
					Toast.makeText(getApplicationContext(),
							"��� �� ������� ������!", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(),
						"������ ���������� �������.", Toast.LENGTH_SHORT)
						.show();
			} finally {
				// ������� ������ ��������
				delete_server_chat.setEnabled(true);
				delete_server_chat.setText("������� ��� ���� �� �������!");
			}
		}
	}

	public void onBackPressed() {
		Log.i("chat", "+ MainActivity - ����� �� ����������");
		finish();
	}
}
