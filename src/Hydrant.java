import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
public class Hydrant extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private TextArea logArea;
	private JTextField inputText;
	private JTextField portText;
	private JTextField addressText;
	private JComboBox modeComboBox;
	private JToggleButton connectToggleButton;
	private Client client = null;
	private Server server = null;
	public Hydrant() {
		super();
		Runtime.getRuntime().addShutdownHook(new Shutdown());
		this.setSize(740,480);
		this.setTitle("Hydrant");
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		/* IPアドレスの取得 */
		String ip = "UnknownHost";
		try {
			ip = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		/* コンポーネントの配置 */
		Container c = this.getContentPane();
		c.setLayout(new GridLayout(1,2));
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JPanel p4 = new JPanel();

		p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));

		/*  p3 */
		p3.setBorder(BorderFactory.createTitledBorder("Setting"));
		GroupLayout groupLayout1 = new GroupLayout(p3);
		p3.setLayout(groupLayout1);
		groupLayout1.setAutoCreateGaps(true);
		groupLayout1.setAutoCreateContainerGaps(true);

		JLabel portLabel= new JLabel("Port");
		JLabel addressLabel= new JLabel("Address");
		JLabel modeLabel= new JLabel("Mode");
		String[] mode = {"Server", "Client"};
		modeComboBox= new JComboBox(mode);

		portText = new JTextField("9000");
		addressText = new JTextField(ip);
		portText.setInputVerifier(new IntegerInputVerifier());

		GroupLayout.SequentialGroup hGroup1 = groupLayout1.createSequentialGroup();
		hGroup1.addGroup(groupLayout1.createParallelGroup()
				.addComponent(addressLabel).addComponent(portLabel).addComponent(modeLabel));
		hGroup1.addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(addressText).addComponent(portText).addComponent(modeComboBox));
		groupLayout1.setHorizontalGroup(hGroup1);

		GroupLayout.SequentialGroup vGroup1 = groupLayout1.createSequentialGroup();
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(addressLabel).addComponent(addressText));
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(portLabel).addComponent(portText));
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(modeLabel).addComponent(modeComboBox));
		groupLayout1.setVerticalGroup(vGroup1);

		/* log */
		logArea = new TextArea("", 15, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);
		logArea.setEditable(false);

		String systemInfo = System.getProperty("os.name") + " " + System.getProperty("os.version")
		+ " (JRE:" + System.getProperty("java.version") + ")";
		this.printLogMessage("Hydrant Preview (20101219)");
		this.printLogMessage(systemInfo);
		this.printLogMessage("");

		inputText = new JTextField(25);
		inputText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] input = inputText.getText().split(" ");
				for(int i = 0; i < input.length; i++){
					if(server != null && modeComboBox.getSelectedIndex() == 0){
						server.sendToAllClient(input[i]);
					}else if(client != null && modeComboBox.getSelectedIndex() == 1){
						client.sendToServer(input[i]);
					}
				}
			}
		});
		/* p4 */
		p4.setBorder(BorderFactory.createTitledBorder("Log"));
		//p4.setLayout(new BoxLayout(p4, BoxLayout.Y_AXIS));
		p4.add(logArea);
		p4.add(inputText);

		/* p1 */
		JLabel appImageIcon = new JLabel(new ImageIcon(this.getClass().getResource("application.png")));
		connectToggleButton = new JToggleButton("OFF");
		appImageIcon.setPreferredSize(new Dimension(320, 380));
		connectToggleButton.setPreferredSize(new Dimension(290, 50));
		connectToggleButton.addActionListener(this);

		p1.add(appImageIcon);
		p1.add(connectToggleButton);

		p2.add(p3);
		p2.add(p4);
		c.add(p1);
		c.add(p2);
		p1.setBackground(Color.WHITE);
		p2.setBackground(Color.WHITE);
		p3.setBackground(Color.WHITE);
		p4.setBackground(Color.WHITE);

		this.setVisible(true);
	}
	public static void main(String[] args) {
		new Hydrant();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		JToggleButton b = (JToggleButton)e.getSource();
		if(b.isSelected()) {
			b.setText("ON");
			addressText.setEnabled(false);
			portText.setEnabled(false);
			modeComboBox.setEnabled(false);
			logArea.setText("");
			if(modeComboBox.getSelectedIndex() == 0) {
				server = new Server(this, portText.getText());
				server.start();
			}else {
				client = new Client(this, addressText.getText(), portText.getText());
			}
		}else {
			b.setText("OFF");
			addressText.setEnabled(true);
			portText.setEnabled(true);
			modeComboBox.setEnabled(true);
			if(client != null) {
				client.close();
				client = null;
			}
			if(server != null) {
				server.dispose();
				server = null;
			}
		}
	}

	public void sendToClient(String s) {
	}
	public void printLogMessage(String s) {
		logArea.append(s + "\n");
	}
}
class IntegerInputVerifier extends InputVerifier{
	@Override
	public boolean verify(JComponent c) {
			boolean verified = false;
			JTextField textField = (JTextField)c;
			try{
				Integer.parseInt(textField.getText());
				verified = true;
			}catch(NumberFormatException e) {
				UIManager.getLookAndFeel().provideErrorFeedback(c);
			}
			return verified;
	}
}
class Shutdown extends Thread {
	@Override
	public void run() {

	}
}
