package ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import projectsettings.ProjectSettingsController;
import serviceNow.NSRolesRestServiceController;
import serviceNow.SendObject;

import javax.swing.*;
import java.awt.event.*;

public class CredentialsUI extends JDialog
{
    private JPanel contentPane;
    private JButton nextButton;
    private JButton cancelButton;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel emailLabel;
    private JLabel environmentLabel;
    private JTextField txtUrl;
    private Project project;
    private serviceNow.SNClient SNClient;

    public CredentialsUI(Project project)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(nextButton);

        this.SNClient = SNClient;
        this.project = project;
//        this.url = txtUrl.getText();

        ProjectSettingsController projectSettingsController = new ProjectSettingsController(this.project);

        if (projectSettingsController.hasAllProjectSettings())
        {
            String nsPassword = projectSettingsController.getProjectPassword();
            if (nsPassword != null && !nsPassword.isEmpty())
            {
                emailField.setText(projectSettingsController.getNsEmail());
                passwordField.setText(nsPassword);
            }
        }

        nextButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onNext();
            }
        });

        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void saveProjectSettings()
    {
        String email = emailField.getText();
        String url = txtUrl.getText();
        String password = String.valueOf(passwordField.getPassword());

        ProjectSettingsController nsProjectSettingsController = new ProjectSettingsController(this.project);
        nsProjectSettingsController.setNsEmail(emailField.getText());
        nsProjectSettingsController.setNsEnvironment(txtUrl.getText());
        nsProjectSettingsController.saveProjectPassword(email, password, url);
    }

    private void onNext()
    {
        if (emailField.getText().isEmpty() || String.valueOf(passwordField.getPassword()).isEmpty() || txtUrl.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Email, Password and Environment are required", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.setVisible(false);


        NSRolesRestServiceController nsRolesRestServiceController = new NSRolesRestServiceController();

        SendObject sObj = new SendObject("authenticate", "");
        String nsAccounts = nsRolesRestServiceController.getNSAccounts(emailField.getText(), String.valueOf(passwordField.getPassword()), txtUrl.getText(), sObj);

        JSONObject jsonObj = null;
        try
        {
            jsonObj = new JSONObject(nsAccounts);
            nsAccounts = jsonObj.getString("result");
        }
        catch (JSONException e)
        {
            nsAccounts = "false";
            e.printStackTrace();
        }


        if (nsAccounts == null || nsAccounts != "true")
        {
            JOptionPane.showMessageDialog(null, "Error getting NetSuite Accounts from Roles Rest Service.\nPlease verify that your e-mail and password are correct.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            saveProjectSettings();
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("<h3>NetSuite Project Settings Updated!</h3>", MessageType.INFO, null)
                    .setFadeoutTime(3000)
                    .createBalloon()
                    .show(RelativePoint.getNorthEastOf(WindowManager.getInstance().getIdeFrame(project).getComponent()),
                            Balloon.Position.above);
        }


        //AccountsUI accountsUI = new AccountsUI(emailField.getText(), String.valueOf(passwordField.getPassword()), txtUrl.getText(), this.project);
//
//        if (accountsUI.getNsAccounts() != null) {
//
//        }

        dispose();
    }

    private void onCancel()
    {
        dispose();
    }
}
