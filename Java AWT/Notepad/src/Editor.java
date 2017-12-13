import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

class Editor extends WindowAdapter implements ActionListener, MouseListener, TextListener {
	public static void main(String args[]) {
		new Editor();
	}

	final Frame f1;
	int end = 0, pos = 0, start = 0;
	Frame f, f2, fsave;
	MenuBar mb;
	Menu m1, m2;
	MenuItem nw, opn, ext, sv, svas, find, fnr;
	Panel p, p2, sp1, sp2;
	Label l, rl, savel;
	Button fb, fcancel, rb, rab, save, dsave, scancel;
	final TextArea t;
	final TextField ft, rt;
	boolean askToSave = false, exitflag = false, openflag = false, newflag = false, hasBeenSaved = false;
	String str, fnd, rep, dir, fname;
	Pattern pat;

	Matcher m;

	public Editor() {
		f = new Frame("Untitled");
		f1 = new Frame("Find and replace");
		fsave = new Frame("Msg Dialog");
		fsave.setSize(500, 130);
		f1.setSize(500, 130);
		f.setSize(800, 400);
		f1.setLayout(new GridLayout(3, 0));
		fsave.setResizable(false);
		fsave.setLayout(new GridLayout(2, 0));
		savel = new Label("Do you Want to Save Changes ? ");
		sp2 = new Panel();
		sp2.setLayout(new GridLayout(0, 3));

		save = new Button("Save");
		save.addActionListener(this);
		dsave = new Button("Don't Save");
		dsave.addActionListener(this);
		scancel = new Button("Cancel");
		scancel.addActionListener((e) -> fsave.setVisible(false));
		sp2.add(save);
		sp2.add(dsave);
		sp2.add(scancel);
		fsave.add(savel);
		fsave.add("South", sp2);
		mb = new MenuBar();
		m1 = new Menu("File");
		m2 = new Menu("Edit");

		nw = new MenuItem("New");
		opn = new MenuItem("Open");
		sv = new MenuItem("Save");
		svas = new MenuItem("Save As");
		ext = new MenuItem("Exit");
		find = new MenuItem("Find");
		fnr = new MenuItem("Find & Replace");
		nw.addActionListener(this);
		opn.addActionListener(this);
		sv.addActionListener(this);
		svas.addActionListener(this);
		ext.addActionListener(this);
		find.addActionListener(this);
		fnr.addActionListener(this);

		m2.add(find);
		m2.add(fnr);
		m1.add(nw);
		m1.add(opn);
		m1.add(sv);
		m1.add(svas);
		m1.addSeparator();
		m1.add(ext);
		mb.add(m1);
		mb.add(m2);
		t = new TextArea("txtArea");
		t.setText(null);
		t.addTextListener(this);
		t.addMouseListener(this);
		p = new Panel();
		p2 = new Panel();
		f.setMenuBar(mb);

		f.add(t);
		l = new Label("Find what :");
		ft = new TextField(20);
		fb = new Button("Find Next");

		fb.addActionListener(this);

		fcancel = new Button("Cancel");
		fcancel.addActionListener((e) -> f1.setVisible(false));
		// f1.setLayout(new FlowLayout());
		p.add(l);
		p.add(ft);
		p.add(fb);
		f1.add(p);
		rl = new Label("Replace :");
		rt = new TextField(20);
		rb = new Button("Replace");
		rb.addActionListener(this);
		rab = new Button("Replace All");
		rab.addActionListener((e) -> {
									Pattern p = Pattern.compile(ft.getText());
									Matcher m = p.matcher(t.getText());
									fnd = ft.getText();
									if (fnd.equals("")) {
										JOptionPane.showMessageDialog(f1, "Please Enter what to find !! ");
									} else {
										while (m.find())
											t.setText(m.replaceAll(rt.getText()));
									}
								});
		p2.add(rl);
		p2.add(rt);
		p2.add(rb);
		p2.add(rab);
		f1.add(p2);
		f1.add(fcancel);
		f1.addWindowListener(this);
		f.addWindowListener(this);
		fsave.addWindowListener(this);
		f.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		str = e.getActionCommand();

		if (str.equals("Find")) {
			// f.setEnabled(false);
			Pattern p = Pattern.compile("\\r");
			Matcher m = p.matcher(t.getText());
			while (m.find())
				t.setText(m.replaceAll(""));
			p2.setVisible(false);
			f1.setVisible(true);

		}
		if (str.equals("Find Next")) {
			if (fnd != ft.getText()) {
				pat = Pattern.compile(ft.getText());
				m = pat.matcher(t.getText());
			}
			fnd = ft.getText();
			if (m.find(pos)) {
				end = m.end();
				pos = end;
				t.select(m.start(), m.end());
				f.toFront();
				start = m.start();
			} else {
				JOptionPane.showMessageDialog(f1, "\"" + fnd + "\"" + " not found ");
			}
		}
		if (str.equals("Replace")) {
			int diff = 0;
			try {
				if (fnd != ft.getText()) {
					pat = Pattern.compile(ft.getText());
					m = pat.matcher(t.getText());
				}

				fnd = ft.getText();
				rep = rt.getText();
				if (fnd.equals("")) {
					JOptionPane.showMessageDialog(f1, "Please Enter what to find !! ");
				} else {
					if (t.getSelectedText().equals(fnd)) {
						diff = rep.length() - fnd.length();
						t.replaceRange(rep, start, end);
						f.toFront();
					}
					if (m.find(pos)) {
						end = m.end() + diff;
						pos = end;
						start = m.start() + diff;
						t.select(start, end);
					} else {
						JOptionPane.showMessageDialog(f1, "\"" + fnd + "\"" + " not found ");
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (str.equals("Find & Replace")) {
			Pattern p = Pattern.compile("\\r");
			Matcher m = p.matcher(t.getText());
			while (m.find())
				t.setText(m.replaceAll(""));
			// f.setEnabled(false);
			f1.setVisible(true);
			p2.setVisible(true);
		}
		if (str.equals("New")) {
			t.selectAll();
			if (t.getSelectedText() != null && askToSave) {
				newflag = true;
				fsave.setVisible(true);
			} else {
				hasBeenSaved = false;
				f.setTitle("Untitled");
				// dispose();
				t.setText("");
			}
		}
		if (str.equals("Don't Save")) {
			if (exitflag) {
				f.setVisible(false);
				f.dispose();
				exitflag = false;
				System.exit(1);
			}
			if (openflag) {
				try {
					askToSave = false; // correction made here
					fsave.setVisible(false);
					FileDialog fd = new FileDialog(f, "Open File", FileDialog.LOAD);
					fd.setVisible(true);
					dir = fd.getDirectory();
					fname = fd.getFile();
					if (fname == null) {
					} else {
						FileInputStream fis = new FileInputStream(dir + fname);
						BufferedReader br = new BufferedReader(new InputStreamReader(fis));
						String str = "", msg = "";
						while ((str = br.readLine()) != null) {
							msg = msg + str;
							msg += "\n";
						}
						t.setText(msg);
						br.close();
						f.setTitle(fname);
						hasBeenSaved = true;
					}
				} catch (Exception e1) {
				}
				// openflag=false;
				newflag = false;
			}
			if (newflag) {
				t.setText("");
				newflag = false;
				askToSave = false;
				f.setTitle("Untitled");
			}
			fsave.setVisible(false);
		}
		if (str.equals("Open")) {
			t.selectAll();
			openflag = true;
			if (t.getSelectedText() != null && askToSave) {
				fsave.setVisible(true);
			} else {
				try {
					fsave.setVisible(false);
					FileDialog fd = new FileDialog(f, "Open File", FileDialog.LOAD);
					fd.setVisible(true);
					dir = fd.getDirectory();
					fname = fd.getFile();
					if (fname == null) {
					} else {
						FileInputStream fis = new FileInputStream(dir + fname);
						BufferedReader br = new BufferedReader(new InputStreamReader(fis));
						String str = "", msg = "";
						while ((str = br.readLine()) != null) {
							msg = msg + str;
							msg += "\n";
						}
						t.setText(msg);
						br.close();
						f.setTitle(fname);
						newflag = false;
						askToSave = false;
						hasBeenSaved = true;

					}
				} catch (Exception e1) {
				}
			}
		}
		if (str.equals("Save")) {
			try {
				fsave.setVisible(false);
				if (hasBeenSaved) {
					FileDialog fd = new FileDialog(f, "Save File", FileDialog.SAVE);
					fd.setVisible(false);
					String txt = t.getText();
					// String dir=fd.getDirectory();
					// String fname=fd.getFile();
					FileOutputStream fos = new FileOutputStream(dir + fname);
					DataOutputStream dos = new DataOutputStream(fos);
					dos.writeBytes(txt);
					dos.close();
					askToSave = false;
					hasBeenSaved = true;
				} else {
					FileDialog fd = new FileDialog(f, "Save File", FileDialog.SAVE);
					fd.setVisible(true);
					String txt = t.getText();
					dir = fd.getDirectory();
					fname = fd.getFile();
					if (fname == null) {
						// askToSave=true;
						hasBeenSaved = false;
						newflag = false;
						openflag = false;
						exitflag = false;
					} else {
						FileOutputStream fos = new FileOutputStream(dir + fname);
						DataOutputStream dos = new DataOutputStream(fos);
						dos.writeBytes(txt);
						dos.close();
						askToSave = false;
						hasBeenSaved = true;
						f.setTitle(fname);
					}
				}
				if (newflag) {
					t.setText("");
					newflag = false;
					askToSave = false;
					f.setTitle("Untitled");
					fsave.setVisible(false);
				}
				if (openflag) {
					try {
						fsave.setVisible(false);
						FileDialog fd = new FileDialog(f, "Open File", FileDialog.LOAD);
						fd.setVisible(true);
						dir = fd.getDirectory();
						fname = fd.getFile();
						if (fname == null) {
						} else {
							FileInputStream fis = new FileInputStream(dir + fname);
							BufferedReader br = new BufferedReader(new InputStreamReader(fis));
							String str = "", msg = "";
							while ((str = br.readLine()) != null) {
								msg = msg + str;
								msg += "\n";
							}
							t.setText(msg);
							br.close();
							f.setTitle(fname);
						}
					} catch (Exception e1) {
					}
					openflag = false;
					newflag = false;
				}
				if (exitflag) {
					f.setVisible(false);
					f.dispose();
					exitflag = false;
					System.exit(1);
				}
			} catch (Exception e2) {
			}
		}
		if (str.equals("Save As")) {
			try {
				FileDialog fd = new FileDialog(f, "Save File", FileDialog.SAVE);
				fd.setVisible(true);
				String txt = t.getText();
				dir = fd.getDirectory();
				fname = fd.getFile();
				if (fname == null) {
				} else {
					FileOutputStream fos = new FileOutputStream(dir + fname);
					DataOutputStream dos = new DataOutputStream(fos);
					dos.writeBytes(txt);
					dos.close();
					askToSave = false;
					hasBeenSaved = true;
					f.setTitle(fname);
				}
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
		if (str.equals("Exit")) {
			t.selectAll();
			exitflag = true;
			if (t.getSelectedText() != null && askToSave) {
				fsave.setVisible(true);
			} else {
				f.setVisible(false);
				f.dispose();
				System.exit(1);
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == t) {
			pos = t.getCaretPosition();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void textValueChanged(TextEvent e) {
		askToSave = true;
		if (t.getText().equals("") && f.getTitle().equals("Untitled")) {
			newflag = false;
			askToSave = false;
		} else {
			// openflag=true;
			askToSave = true;
			// newflag=true;
		}
		if (openflag) {
			askToSave = false;
			openflag = false;
		}
	}

	public void windowClosing(WindowEvent e) {
		Window w = e.getWindow();
		if (e.getSource() == f1) {
			w.setVisible(false);
			w.dispose();
		}
		if (e.getSource() == fsave) {
			w.setVisible(false);
			w.dispose();
		}
		if (e.getSource() == f) {
			exitflag = true;
			t.selectAll();
			if (t.getSelectedText() != null && askToSave) {
				fsave.setVisible(true);
			} else {
				w.setVisible(false);
				w.dispose();
				System.exit(1);
			}
		}
		// f.setEnabled(true);
	}
}
