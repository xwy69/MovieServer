package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

public class EditInfoWindow extends JDialog implements ActionListener{
    DataOutputStream remoteOut;
    JTextField path;
    JFileChooser jfc;
    JPanel panelUp;
    JPanel panelDown;
    JLabel l;
    Boolean isPicture = false;
    EditInfoWindow(String title, byte[] head, String info, DataOutputStream remoteOut){
        panelUp = new JPanel(new BorderLayout());
        panelDown = new JPanel(new BorderLayout());
        ImageIcon headImage = null;
        if(head != null) {
            headImage = new ImageIcon(head);
            headImage.setImage(headImage.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
        }
        JTextArea personalInfo = new JTextArea(info);
        Box up = Box.createHorizontalBox();
        jfc = new JFileChooser();
        path = new JTextField();
        JButton upload = new JButton("Upload");
        up.add(path);
        up.add(upload);
        JButton save = new JButton("Save");

        l = new JLabel(headImage);
        l.setHorizontalAlignment(JLabel.CENTER);
        panelUp.add(new JLabel("Head Image:"), BorderLayout.NORTH);
        panelUp.add(l, BorderLayout.CENTER);
        panelUp.add(up, BorderLayout.SOUTH);

        panelDown.add(new JLabel("Personal Description:"), BorderLayout.NORTH);
        panelDown.add(personalInfo, BorderLayout.CENTER);
        panelDown.add(save, BorderLayout.SOUTH);

        Container content = getContentPane();      // Get content pane
        content.setLayout(new GridLayout(2,1));
        content.add(panelUp);
        content.add(panelDown);

        upload.addActionListener(this);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fname = path.getText();
                try {
                    if(isPicture) {
                        FileInputStream fis = new FileInputStream(fname);
                        int size = fis.available();
                        byte[] data = new byte[size];
                        fis.read(data);
                        remoteOut.writeUTF("P");
                        remoteOut.writeInt(size);
                        remoteOut.write(data);
                        remoteOut.flush();
                    }
                    remoteOut.writeUTF("F"+personalInfo.getText());
                    remoteOut.flush();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String fname = path.getText();
            File f = new File(fname);
            boolean flag = isImage(f);
            if(!flag){
                JOptionPane.showMessageDialog(null, "Not an image.","Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            panelUp.remove(l);
            ImageIcon headImage = new ImageIcon(fname);
            headImage.setImage(headImage.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));
            l = new JLabel(headImage);
            l.setHorizontalAlignment(JLabel.CENTER);
            panelUp.add(l, BorderLayout.CENTER);
            panelUp.revalidate();
            isPicture = true;
        }
        catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private boolean isImage(File file)
    {
        boolean flag = false;
        try
        {
            ImageInputStream is = ImageIO.createImageInputStream(file);
            if(null == is)
            {
                return flag;
            }
            is.close();
            flag = true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }
}