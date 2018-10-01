/**
 * @SNType sys_script_include
 * @SNApp test_plugin
 * @SNName testing_include
 * @SNFileID d840e57e4fb86300855601bda310c72f
 * @SNApiName x_174422_test_plug.testing_include
 */

var grs = new GlideRecordSecure("sys_script_include");
grs.query("sys_id", "d840e57e4fb86300855601bda310c72f");
var count = 0;
while (grs.next())
{
//        grs.val = "val - " + grs.id;
    gs.info(grs.getValue("script"));
    count++
}