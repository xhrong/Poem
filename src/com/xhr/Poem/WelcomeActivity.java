package com.xhr.Poem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class WelcomeActivity extends Activity {

    private static boolean isCanceled = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AppState.setClassicPoems(AppState.getPoemAccess().getAllPoems());

//                    for(PoemItem item :AppState.getClassicPoems()){
////                        String content=item.getContent();
////
////                        content=content.replace("。","。<br>");
////                        if(content.endsWith("<br>")){
////                            content=content.substring(0,content.length()-4);
////                        }
//
//                        String notation=item.getNotation();
//                        notation=notation.replace("</p><p>  </p><p>","<br>").replace("</p><p>","<br>").replace("<p>","").replace("</p>","");
//                        String translation=item.getTranslation();
//                        translation=translation.replace("</p><p>  </p><p>","<br>").replace("</p><p>","<br>").replace("<p>","").replace("</p>","");
//
//                        String analysis=item.getAnalysis();
//                        analysis=analysis.replace("</p><p>  </p><p>","<br>").replace("</p><p>","<br>").replace("<p>","").replace("</p>","");
//
//                        translation=translation.replace("<br>","");
//                        analysis=analysis.replace("<br>","");
//
//                        notation=notation.replace(" ","");
//                        translation=translation.replace(" ","");
//                        analysis=analysis.replace(" ","");
//
//                     //   item.setContent(content);
//                        item.setNotation(notation);
//                        item.setTranslation(translation);
//                        item.setAnalysis(analysis);
//
//                        poemAccess.updatePoem(item);
//
//                    }
                    Thread.sleep(1000);
                    if (!isCanceled) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    WelcomeActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCanceled = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isCanceled = true;
    }

}
