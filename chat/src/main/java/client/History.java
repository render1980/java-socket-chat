package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public final class History extends JFrame {

	private static final long serialVersionUID = -285749384623259399L;
	private static JTextArea historyTextArea;

	private static History instance = null;

	public static History getInstance() {
		if (instance == null) {
			instance = new History();
		}
		return instance;
	}

	private History() {
		setVisible(true);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollHistory = new JScrollPane();
		scrollHistory
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollHistory.setBounds(0, 0, 434, 262);
		contentPane.add(scrollHistory);

		setHistoryTextArea(new JTextArea());
		getHistoryTextArea().setEditable(false);
		getHistoryTextArea().setLineWrap(true);
		getHistoryTextArea().setWrapStyleWord(true);
		scrollHistory.setViewportView(getHistoryTextArea());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				instance = null;
				dispose();
			}
		});
	}
	
	public static void setHistoryTextArea(String args){
		historyTextArea.setText(args);
	}

	public static JTextArea getHistoryTextArea() {
		return historyTextArea;
	}

	public static void setHistoryTextArea(JTextArea historyTextArea) {
		History.historyTextArea = historyTextArea;
	}
}
