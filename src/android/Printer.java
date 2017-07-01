package cordova.printer.multilangprint;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import zj.com.command.sdk.PrintPicture;
import zj.com.customize.sdk.Other;

import mmsl.GetPrintableImage.GetPrintableImage;

public class Printer extends CordovaPlugin{
    int[] prnRasterImg;
    float fontSize = 22.0f;
    int typeface = Typeface.NORMAL;
    int alignment = Cocos2dxBitmap.ALIGNLEFT;
    String familyName = "monospace";
    int image_width;
    int image_height;
    private static boolean is58mm = true;
//String gujbharat="ભારત";
    GetPrintableImage gpi;
    Context ctx;
    byte[] CommandImagePrint;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if(action.equals("print")){
            try {
                String resposeText =  args.getString(0);
                Cocos2dxBitmap cocos2dxBitmap = new Cocos2dxBitmap();
                Bitmap ecgBmp1 = cocos2dxBitmap.createTextBitmap(resposeText,
                        familyName, typeface, (int) fontSize, alignment, 380, 2000);
                GetPrintableImage pt = new GetPrintableImage();
                prnRasterImg = pt.GetPrintableArray(ctx, ecgBmp1.getWidth(),
                        ecgBmp1);
                image_height = pt.getPrintHeight();
                image_width = pt.getPrintWidth();

                try {
                    // sleep(1000);

                    CommandImagePrint= new byte[prnRasterImg.length + 5];

                    CommandImagePrint[0] = 0x1B; // Command to for bit image
                    // mode
                    // please refer the previous

                    // document
                    CommandImagePrint[1] = 0x23; // Exc #
                    CommandImagePrint[2] = (byte) image_width; // 8 Vertical
                    // Dots(Heights)
                    // &
                    // Single Width
                    // Mode

                    // selected
                    CommandImagePrint[3] = (byte) (image_height / 256);// f8 //
                    // Decimal
                    // 248
                    // since
                    // the
                    // Image
                    // width
                    // is

                    // 248 Pixels as mentioned above
                    CommandImagePrint[4] = (byte) (image_height % 256);

                    for (int i = 0; i < prnRasterImg.length; i++) {
                        CommandImagePrint[i + 5] = (byte) (prnRasterImg[i] & 0xFF);
                    }



                } catch (Exception ioe) {
                    System.out
                            .println("Problems reading from or writing to serial port."
                                    + ioe.getMessage());

                }

                callbackContext.success(CommandImagePrint);
            }catch (JSONException e){
                callbackContext.error("Failed to Parse Parameters");
            }
            return true;
        }else{
        	Bitmap bm1 = getImageFromAssetsFile("demo.jpg");
        	Bitmap bmp = Other.createAppIconText(bm1,args.getString(0),25,is58mm,45);
			int nMode = 0;
			int nPaperWidth = 384;
			
			if(bmp != null)
			{
				byte[] data = PrintPicture.POS_PrintBMP(bmp, nPaperWidth, nMode);
				/*SendDataByte(Command.ESC_Init);
				SendDataByte(Command.LF);
				SendDataByte(data);
				SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
				SendDataByte(PrinterCommand.POS_Set_Cut(1));
				SendDataByte(PrinterCommand.POS_Set_PrtInit());*/
				callbackContext.success(data);
			}
        }
        return false;
    }
    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    private Bitmap getImageFromAssetsFile(String fileName) {
		Bitmap image = null;
		AssetManager am = this.cordova.getActivity().getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;

	}
}
