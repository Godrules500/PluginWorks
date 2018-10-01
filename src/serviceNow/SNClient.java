package serviceNow;

import com.intellij.openapi.project.Project;
import org.codehaus.jettison.json.JSONObject;
import projectsettings.ProjectSettingsController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;

public class SNClient
{
    private NSAccount nsAccount;

    private String nsEnvironment;
    private String nsUserName;
    NSRolesRestServiceController nsRolesRestServiceController = new NSRolesRestServiceController();
    ProjectSettingsController settings;

    public SNClient(String environment, String userName)
    {
//        this.nsAccount = account;
        this.nsEnvironment = environment;
        this.nsUserName = userName;

        String nsWebServiceURL = environment;
//        nsWebServiceURL = this.nsAccount.getProductionWebServicesDomain();

        // In order to use SSL forwarding for SOAP messages. Refer to FAQ for details
        System.setProperty("axis.socketSecureFactory", "org.apache.axis.components.net.SunFakeTrustSocketFactory");
    }

    public NSAccount getNSAccount()
    {
        return this.nsAccount;
    }

    //    private Passport createPassport() {
//        RecordRef role = new RecordRef();
//        role.setInternalId(this.nsAccount.getRoleId());
//
//        Passport passport = new Passport();
//        passport.setEmail(this.nsAccount.getAccountEmail());
//        passport.setPassword(this.nsAccount.getAccountPassword());
//        passport.setAccount(this.nsAccount.getAccountId());
//        passport.setRole(role);
//        return passport;
//    }
//
    public void tryToLogin() throws RemoteException
    {
//        Passport passport = createPassport();
//        Status status = (_port.login(passport)).getStatus();
//
//        if (!status.isIsSuccess()) {
//            throw new IllegalStateException(new Throwable("Netsuite SuiteTalk login request call was unsuccessful."));
//        }
    }

    public File downloadFile(String fileData, Project project) throws RemoteException
    {
        try
        {
            settings = new ProjectSettingsController(project);
            String password = settings.getProjectPassword();

            SendObject sObj = new SendObject("compare", fileData);
            String response = nsRolesRestServiceController.getNSAccounts(this.nsUserName, password, this.nsEnvironment, sObj);

            JSONObject jsonObj = new JSONObject(response);

            // print result
            return new File(jsonObj.getString("result"));

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public String uploadFile(File file, Project project) throws RemoteException
    {
        try
        {

            String fileData = loadFile(file.getName()).toString();
            settings = new ProjectSettingsController(project);
            String password = settings.getProjectPassword();

            SendObject sObj = new SendObject("upload", fileData);
            String response = nsRolesRestServiceController.getNSAccounts(this.nsUserName, password, this.nsEnvironment, sObj);

            JSONObject jsonObj = new JSONObject(response);

            // print result
            return jsonObj.getString("result");

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private byte[] loadFile(String sFileName)
    {
        InputStream inFile = null;
        byte[] data = null;

        try
        {
            File file = new File(sFileName);
            inFile = new FileInputStream(file);
            data = new byte[(int) file.length()];
            inFile.read(data, 0, (int) file.length());
            inFile.close();
        }
        catch (Exception ex)
        {
            return null;
        }

        return data;
    }

//    public String searchFile(String fileName, String parentFolderId, String projectSettingsRootFolderId) throws RemoteException {
//        RecordRef parentFolderRef = new RecordRef();
//        parentFolderRef.setInternalId(parentFolderId);
//
//        RecordRef[] rr = new RecordRef[1];
//        rr[0] = parentFolderRef;
//
//        SearchMultiSelectField smsf = new SearchMultiSelectField();
//        smsf.setSearchValue(rr);
//        smsf.setOperator(SearchMultiSelectFieldOperator.anyOf);
//
//        SearchStringField nameField = new SearchStringField();
//        nameField.setOperator(SearchStringFieldOperator.is);
//        nameField.setSearchValue(fileName);
//
//        FileSearchBasic fileSearchBasic = new FileSearchBasic();
//        fileSearchBasic.setFolder(smsf);
//        fileSearchBasic.setName(nameField);
//
//        SearchResult results = null;
//
//        try {
//            results = _port.search(fileSearchBasic);
//        } catch (Exception ex) {
//            return null;
//        }
//
//        if (results != null && results.getStatus().isIsSuccess()) {
//            RecordList myRecordlist = results.getRecordList();
//
//            if (myRecordlist != null && myRecordlist.getRecord() != null) {
//                File foundFile = null;
//
//                if (parentFolderId.equals(projectSettingsRootFolderId)) {
//                    foundFile = (File) myRecordlist.getRecord(results.getTotalRecords()-1);
//                } else {
//                    foundFile = (File) myRecordlist.getRecord(0);
//                }
//
//                if (foundFile != null) {
//                    return foundFile.getInternalId();
//                }
//            }
//        }
//
//        return null;
//    }
}
