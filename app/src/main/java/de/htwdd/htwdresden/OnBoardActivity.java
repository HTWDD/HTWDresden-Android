package de.htwdd.htwdresden;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import de.htwdd.htwdresden.account.AuthenticatorActivity;

public class OnBoardActivity extends TutorialActivity {

    String animation = "{\"v\":\"4.9.0\",\"fr\":29.9700012207031,\"ip\":0,\"op\":150.000006109625,\"w\":100,\"h\":100,\"nm\":\"tap-button\",\"ddd\":0,\"assets\":[],\"layers\":[{\"ddd\":0,\"ind\":1,\"ty\":4,\"nm\":\"Layer 1/tap-button Outlines\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":72},\"r\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":0,\"s\":[0],\"e\":[1800]},{\"t\":149.000006068894}]},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[18.777,0],[0,18.777],[-18.778,0],[0,-18.778]],\"o\":[[-18.778,0],[0,-18.778],[18.777,0],[0,18.777]],\"v\":[[0,34],[-34,0],[0,-34],[34,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[19.882,0],[0,-19.882],[-19.882,0],[0,19.882]],\"o\":[[-19.882,0],[0,19.882],[19.882,0],[0,-19.882]],\"v\":[[0,-36],[-36,0],[0,36],[36,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":2,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"},{\"ty\":\"gf\",\"o\":{\"a\":0,\"k\":99},\"r\":1,\"g\":{\"p\":3,\"k\":{\"a\":0,\"k\":[0,1,1,1,0.5,1,1,1,1,1,1,1,0.193,0,0.308,0.5,0.423,1,0.507,1,0.592,1,0.716,0.5,0.84,0]}},\"s\":{\"a\":0,\"k\":[0,0]},\"e\":{\"a\":0,\"k\":[100,0]},\"t\":1,\"nm\":\"Gradient Fill 1\",\"mn\":\"ADBE Vector Graphic - G-Fill\"}],\"ip\":0,\"op\":150.000006109625,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":2,\"ty\":4,\"nm\":\"Layer 4 Outlines\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":10},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[16.028,0],[0,-19.882],[-1.145,-3.512],[-27.666,19.848]],\"o\":[[-19.882,0],[0,3.896],[15.085,0.642],[-4.663,-14.477]],\"v\":[[0.867,-23.617],[-35.133,12.383],[-33.362,23.538],[35.134,1.339]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"mm\",\"mm\":4,\"nm\":\"Merge Paths 1\",\"mn\":\"ADBE Vector Filter - Merge\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[1,1,1,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[49.133,37.617],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":0,\"op\":153.000006231818,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":3,\"ty\":4,\"nm\":\"Layer 2 Outlines\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[0,19.882],[19.882,0],[0,-19.882],[-19.882,0]],\"o\":[[0,-19.882],[-19.882,0],[0,19.882],[19.882,0]],\"v\":[[36,0],[0,-36],[-36,0],[0,36]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.9490196,0.3843137,0.2941177,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":0,\"k\":[100,100],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":0,\"k\":100,\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":2,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":0,\"op\":153.000006231818,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":4,\"ty\":4,\"nm\":\"Layer 3 Outlines 11\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":135,\"s\":[85,85],\"e\":[120,120]},{\"t\":165.000006720588}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":135,\"s\":[100],\"e\":[0]},{\"t\":165.000006720588}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":135.000005498663,\"op\":166.000006761319,\"st\":135.000005498663,\"bm\":0},{\"ddd\":0,\"ind\":5,\"ty\":4,\"nm\":\"Layer 3 Outlines 10\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":120,\"s\":[85,85],\"e\":[120,120]},{\"t\":150.000006109625}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":120,\"s\":[100],\"e\":[0]},{\"t\":150.000006109625}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":120.0000048877,\"op\":151.000006150356,\"st\":120.0000048877,\"bm\":0},{\"ddd\":0,\"ind\":6,\"ty\":4,\"nm\":\"Layer 3 Outlines 9\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":105,\"s\":[85,85],\"e\":[120,120]},{\"t\":135.000005498663}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":105,\"s\":[100],\"e\":[0]},{\"t\":135.000005498663}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":105.000004276738,\"op\":136.000005539394,\"st\":105.000004276738,\"bm\":0},{\"ddd\":0,\"ind\":7,\"ty\":4,\"nm\":\"Layer 3 Outlines 8\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":90,\"s\":[85,85],\"e\":[120,120]},{\"t\":120.0000048877}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":90,\"s\":[100],\"e\":[0]},{\"t\":120.0000048877}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":90.0000036657751,\"op\":121.000004928431,\"st\":90.0000036657751,\"bm\":0},{\"ddd\":0,\"ind\":8,\"ty\":4,\"nm\":\"Layer 3 Outlines 7\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":75,\"s\":[85,85],\"e\":[120,120]},{\"t\":105.000004276738}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":75,\"s\":[100],\"e\":[0]},{\"t\":105.000004276738}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":75.0000030548126,\"op\":106.000004317469,\"st\":75.0000030548126,\"bm\":0},{\"ddd\":0,\"ind\":9,\"ty\":4,\"nm\":\"Layer 3 Outlines 6\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":60,\"s\":[85,85],\"e\":[120,120]},{\"t\":90.0000036657751}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":60,\"s\":[100],\"e\":[0]},{\"t\":90.0000036657751}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":60.0000024438501,\"op\":91.000003706506,\"st\":60.0000024438501,\"bm\":0},{\"ddd\":0,\"ind\":10,\"ty\":4,\"nm\":\"Layer 3 Outlines 5\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":45,\"s\":[85,85],\"e\":[120,120]},{\"t\":75.0000030548126}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":45,\"s\":[100],\"e\":[0]},{\"t\":75.0000030548126}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":45.0000018328876,\"op\":76.0000030955434,\"st\":45.0000018328876,\"bm\":0},{\"ddd\":0,\"ind\":11,\"ty\":4,\"nm\":\"Layer 3 Outlines 4\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":30,\"s\":[85,85],\"e\":[120,120]},{\"t\":60.0000024438501}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":30,\"s\":[100],\"e\":[0]},{\"t\":60.0000024438501}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":30.0000012219251,\"op\":61.0000024845809,\"st\":30.0000012219251,\"bm\":0},{\"ddd\":0,\"ind\":12,\"ty\":4,\"nm\":\"Layer 3 Outlines 3\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":15,\"s\":[85,85],\"e\":[120,120]},{\"t\":45.0000018328876}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":15,\"s\":[100],\"e\":[0]},{\"t\":45.0000018328876}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":15.0000006109625,\"op\":46.0000018736184,\"st\":15.0000006109625,\"bm\":0},{\"ddd\":0,\"ind\":13,\"ty\":4,\"nm\":\"Layer 3 Outlines 2\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":0,\"s\":[85,85],\"e\":[120,120]},{\"t\":30.0000012219251}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":0,\"s\":[100],\"e\":[0]},{\"t\":30.0000012219251}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":0,\"op\":31.0000012626559,\"st\":0,\"bm\":0},{\"ddd\":0,\"ind\":14,\"ty\":4,\"nm\":\"Layer 3 Outlines\",\"sr\":1,\"ks\":{\"o\":{\"a\":0,\"k\":100},\"r\":{\"a\":0,\"k\":0},\"p\":{\"a\":0,\"k\":[50,50,0]},\"a\":{\"a\":0,\"k\":[50,50,0]},\"s\":{\"a\":0,\"k\":[100,100,100]}},\"ao\":0,\"shapes\":[{\"ty\":\"gr\",\"it\":[{\"ind\":0,\"ty\":\"sh\",\"ix\":1,\"ks\":{\"a\":0,\"k\":{\"i\":[[22.092,0],[0,-22.091],[-22.091,0],[0,22.092]],\"o\":[[-22.091,0],[0,22.092],[22.092,0],[0,-22.091]],\"v\":[[0,-40],[-40,0],[0,40],[40,0]],\"c\":true}},\"nm\":\"Path 1\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ind\":1,\"ty\":\"sh\",\"ix\":2,\"ks\":{\"a\":0,\"k\":{\"i\":[[-21.505,0],[0,-21.505],[21.505,0],[0,21.505]],\"o\":[[21.505,0],[0,21.505],[-21.505,0],[0,-21.505]],\"v\":[[0,-39],[39,0],[0,39],[-39,0]],\"c\":true}},\"nm\":\"Path 2\",\"mn\":\"ADBE Vector Shape - Group\"},{\"ty\":\"fl\",\"c\":{\"a\":0,\"k\":[0.945098,0.3843137,0.2901961,1]},\"o\":{\"a\":0,\"k\":100},\"r\":1,\"nm\":\"Fill 1\",\"mn\":\"ADBE Vector Graphic - Fill\"},{\"ty\":\"tr\",\"p\":{\"a\":0,\"k\":[50,50],\"ix\":2},\"a\":{\"a\":0,\"k\":[0,0],\"ix\":1},\"s\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833,0.833],\"y\":[0.833,0.833]},\"o\":{\"x\":[0.167,0.167],\"y\":[0.167,0.167]},\"n\":[\"0p833_0p833_0p167_0p167\",\"0p833_0p833_0p167_0p167\"],\"t\":-15,\"s\":[85,85],\"e\":[120,120]},{\"t\":15.0000006109625}],\"ix\":3},\"r\":{\"a\":0,\"k\":0,\"ix\":6},\"o\":{\"a\":1,\"k\":[{\"i\":{\"x\":[0.833],\"y\":[0.833]},\"o\":{\"x\":[0.167],\"y\":[0.167]},\"n\":[\"0p833_0p833_0p167_0p167\"],\"t\":-15,\"s\":[100],\"e\":[0]},{\"t\":15.0000006109625}],\"ix\":7},\"sk\":{\"a\":0,\"k\":0,\"ix\":4},\"sa\":{\"a\":0,\"k\":0,\"ix\":5},\"nm\":\"Transform\"}],\"nm\":\"Group 1\",\"np\":3,\"cix\":2,\"ix\":1,\"mn\":\"ADBE Vector Group\"}],\"ip\":-15.0000006109625,\"op\":16.0000006516934,\"st\":-15.0000006109625,\"bm\":0}]}";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LottieAnimationView ltAnimViewActiveAnalytics;
    LottieAnimationView ltAnimViewInActiveAnalytics;
    LottieAnimationView animationCheckBox;
    LottieAnimationView animationGreenButton;
    LottieAnimationView animationRedButton;
    TextView noteCrashlytics;
    Button buttonPrev;
    Button buttonNext;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setIndicator(R.drawable.indicator_selected_inactive);
        setIndicatorSelected(R.drawable.indicator_selected);
        int i = 0;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

//        if(sharedPreferences.getBoolean("FIRST_RUN", true)){
        if(i!=1){
            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_welcome)
                    .setTitle(getString(R.string.welcome))
                    .setContent(getString(R.string.welcome_text_content))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());
            // Permission Step
            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_analytics)
                    .setTitle(getString(R.string.analytics))
                    .setContent(getString(R.string.analytics_text_content))
                    .setSummary(getString(R.string.analytics_text_summary))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());

            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_crashlogger)
                    .setTitle(getString(R.string.crashlytics))
                    .setContent(getString(R.string.crashlytics_text_content))
                    .setSummary(getString(R.string.crashlytics_text_summary))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());

            addFragment(new Step.Builder()
                    .setTitle(getString(R.string.login_with_img))
                    .setView(R.layout.onboarding_fragment_login)
                    .setContent(getString(R.string.login_text_content))
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .setSummary(getString(R.string.login_text_summary))
                    .build());

            addFragment(new Step.Builder()
                    .setView(R.layout.onboarding_fragment_finish_tutorial)
                    .setTitle("Activate Crashlogger?")
                    .setContent("This is content")
                    .setSummary("This is summary")
                    .setDrawable(R.drawable.htw_logo_round_blue)
                    .build());

            buttonPrev = findViewById(R.id.prev);
            buttonPrev.setTextColor(getResources().getColor(R.color.middle_gray));
            buttonPrev.setVisibility(View.GONE);
            buttonNext = findViewById(R.id.next);
            buttonNext.setTextColor(getResources().getColor(R.color.middle_gray));
            setPrevText("Zurück");
            setNextText("Weiter");
            setFinishText("Fertig");
        }
        else {
            finishTutorial();
        }
    }

    @Override
    public void currentFragmentPosition(int position) {
        if(position == 0) {
            buttonPrev.setVisibility(View.GONE);
        }
        else {
            buttonPrev.setVisibility(View.VISIBLE);
        }

        if(position == 1) {
            ltAnimViewActiveAnalytics = findViewById(R.id.anim_active_analytics);
            ltAnimViewInActiveAnalytics = findViewById(R.id.anim_not_active_analytics);

            if(!sharedPreferences.getBoolean("firebase_analytics.enable", false)){
                ltAnimViewInActiveAnalytics.setEnabled(true);
                ltAnimViewInActiveAnalytics.setOnClickListener(view -> {
                    editor = sharedPreferences.edit();
                    editor.putBoolean("firebase_analytics.enable", true);
                    editor.apply();

                    Boolean test = sharedPreferences.getBoolean("firebase_analytics.enable", false);

                    ltAnimViewInActiveAnalytics.setVisibility(View.GONE);
                    ltAnimViewActiveAnalytics.setVisibility(View.VISIBLE);
                    ltAnimViewActiveAnalytics.playAnimation();
                });
            }
            else {
                ltAnimViewInActiveAnalytics.setVisibility(View.GONE);
                ltAnimViewActiveAnalytics.setVisibility(View.VISIBLE);
            }
        }

        Boolean test = sharedPreferences.getBoolean("firebase_analytics.enable", false);


        if(position == 2) {
            if(sharedPreferences.getBoolean("firebase_analytics.enable", false)){

                animationCheckBox = findViewById(R.id.anim_active_crashlytics);

                if(!sharedPreferences.getBoolean("firebase_crashlytics.enable", false)){
                    animationCheckBox.setEnabled(true);
                    animationCheckBox.setAnimation("success_blue.json");
                    animationCheckBox.playAnimation();
                    animationCheckBox.setOnClickListener(view -> {
                        editor.putBoolean("firebase_crashlytics.enable", true);
                        editor.apply();
                        animationCheckBox.setAnimation("check_mark_success_blue.json");
                        animationCheckBox.loop(false);
                        animationCheckBox.playAnimation();
                        animationCheckBox.setEnabled(false);
                    });
                }
            }
            else {
                animationCheckBox = findViewById(R.id.anim_active_crashlytics);
                animationCheckBox.setAnimation("circle_red_button.json");
                animationCheckBox.loop(true);
                animationCheckBox.playAnimation();
            }
        }


        if(position == 3) {
            btnLogin = findViewById(R.id.btnLogin);

            Account[] accounts = AccountManager.get(getApplicationContext()).getAccountsByType(getString(R.string.auth_type));

            if(accounts.length > 0) {
                btnLogin.setEnabled(false);
            }
            else {
                btnLogin.setEnabled(true);
                btnLogin.setOnClickListener(view ->
                        openLogin());
            }
            if(accounts.length > 0) {
                btnLogin.setEnabled(false);
            }
        }
    }

    void openLogin(){
        Intent intent = new Intent(this, AuthenticatorActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishTutorial() {
        editor.putBoolean("FIRST_RUN", false);

        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
