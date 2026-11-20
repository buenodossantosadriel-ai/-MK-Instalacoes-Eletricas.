package br.com.mk.instalacoes;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.core.content.FileProvider;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    private DB db;
    private LinearLayout root, body;
    private final int navy = Color.rgb(15, 35, 58);
    private final int blue = Color.rgb(24, 116, 190);
    private final int teal = Color.rgb(17, 148, 133);
    private final int ink = Color.rgb(34, 48, 64);
    private final int muted = Color.rgb(105, 119, 135);
    private final int surface = Color.WHITE;
    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        db = new DB(this);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        showHome();
    }

    private GradientDrawable shape(int color, int radius, int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (strokeColor != 0) d.setStroke(dp(1), strokeColor);
        return d;
    }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private LinearLayout.LayoutParams params(int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = dp(bottom);
        return p;
    }
    private void base(String title) {
        root = new LinearLayout(this);
        root.setOrientation(1);
        root.setBackgroundColor(Color.rgb(243, 246, 250));
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(1);
        header.setPadding(dp(22), dp(20), dp(20), dp(20));
        header.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{navy, Color.rgb(25, 75, 112)}));
        TextView brand = new TextView(this);
        brand.setText("MK  /  INSTALAÇÕES ELÉTRICAS");
        brand.setTextColor(Color.rgb(185, 218, 241));
        brand.setTextSize(11);
        brand.setLetterSpacing(0.08f);
        brand.setTypeface(null, Typeface.BOLD);
        header.addView(brand);
        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(23);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, dp(9), 0, 0);
        header.addView(heading);
        root.addView(header);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        body = new LinearLayout(this);
        body.setPadding(dp(18), dp(20), dp(18), dp(30));
        body.setOrientation(1);
        scroll.addView(body);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }
    private TextView text(String s, int size) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(ink);
        t.setPadding(dp(2), dp(7), dp(2), dp(7));
        return t;
    }
    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setHint(hint);
        e.setTextSize(15);
        e.setTextColor(ink);
        e.setHintTextColor(muted);
        e.setPadding(dp(14), dp(11), dp(14), dp(11));
        e.setBackground(shape(Color.WHITE, 13, Color.rgb(220, 228, 237)));
        e.setElevation(dp(1));
        e.setLayoutParams(params(11));
        return e;
    }
    private Button btn(String s, Runnable r) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextSize(15);
        b.setTypeface(null, Typeface.BOLD);
        b.setTextColor(Color.WHITE);
        b.setPadding(dp(16), dp(10), dp(16), dp(10));
        b.setBackground(shape(blue, 14, 0));
        b.setElevation(dp(3));
        b.setStateListAnimator(null);
        b.setOnClickListener(v -> r.run());
        b.setLayoutParams(params(12));
        return b;
    }
    private void add(LinearLayout l, View v) { l.addView(v, new LinearLayout.LayoutParams(-1, -2)); }
    private void section(String title, String subtitle) {
        TextView h = text(title, 18);
        h.setTypeface(null, Typeface.BOLD);
        h.setPadding(0, dp(8), 0, dp(2));
        add(body, h);
        if (subtitle != null) {
            TextView sub = text(subtitle, 13);
            sub.setTextColor(muted);
            sub.setPadding(0, 0, 0, dp(12));
            add(body, sub);
        }
    }
    private void card(LinearLayout parent, View child) {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(1);
        wrap.setPadding(dp(15), dp(13), dp(15), dp(5));
        wrap.setBackground(shape(surface, 17, Color.rgb(230, 235, 241)));
        wrap.setElevation(dp(2));
        wrap.addView(child);
        parent.addView(wrap, params(14));
    }
    private void showHome() {
        base("Painel principal");
        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(1);
        hero.setPadding(dp(18), dp(18), dp(18), dp(18));
        hero.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.rgb(24, 116, 190), Color.rgb(17, 148, 133)}));
        TextView welcome = new TextView(this);
        welcome.setText("Seja bem-vindo!");
        welcome.setTextColor(Color.WHITE);
        welcome.setTextSize(21);
        welcome.setTypeface(null, Typeface.BOLD);
        hero.addView(welcome);
        TextView sub = new TextView(this);
        sub.setText("Organize seus clientes e crie orçamentos profissionais.");
        sub.setTextColor(Color.rgb(235, 247, 255));
        sub.setTextSize(14);
        sub.setPadding(0, dp(6), 0, 0);
        hero.addView(sub);
        card(body, hero);
        section("Acesso rápido", "Escolha o que deseja fazer.");
        add(body, btn("＋  Criar novo orçamento", () -> newQuote()));
        add(body, btn("♙  Clientes", () -> clients()));
        add(body, btn("▦  Materiais e preços", () -> catalog("material")));
        add(body, btn("⚡  Serviços e mão de obra", () -> catalog("servico")));
        add(body, btn("▤  Histórico de orçamentos", () -> history()));
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(1);
        TextView contact = text("WhatsApp / Telefone: 54 99674-7442", 14);
        contact.setTypeface(null, Typeface.BOLD);
        add(info, contact);
        TextView offline = text("✓ Seus dados ficam salvos neste aparelho. O app funciona offline.", 12);
        offline.setTextColor(muted);
        add(info, offline);
        card(body, info);
    }
    private void clients() {
        base("Clientes");
        section("Novo cadastro", "Adicione os dados do cliente.");
        EditText name=field("Nome do cliente"), phone=field("Telefone"), address=field("Endereço");
        add(body,name); add(body,phone); add(body,address);
        add(body,btn("Salvar cliente",()->{
            if(name.getText().toString().trim().isEmpty()){msg("Informe o nome.");return;}
            ContentValues cv=new ContentValues(); cv.put("name",name.getText().toString()); cv.put("phone",phone.getText().toString()); cv.put("address",address.getText().toString());
            db.getWritableDatabase().insert("clients",null,cv); msg("Cliente salvo."); clients();
        }));
        section("Clientes cadastrados", null);
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,phone FROM clients ORDER BY name",null);
        while(c.moveToNext()) {
            LinearLayout item = new LinearLayout(this); item.setOrientation(1);
            TextView n=text(c.getString(1),16); n.setTypeface(null,Typeface.BOLD); add(item,n);
            TextView p=text(c.getString(2),13); p.setTextColor(muted); add(item,p); card(body,item);
        }
        c.close(); add(body,btn("Voltar ao início",()->showHome()));
    }
    private void catalog(String type) {
        String label=type.equals("material")?"Material":"Serviço";
        base(label+"s e preços");
        section("Adicionar "+label.toLowerCase(), "Os preços podem ser alterados quando necessário.");
        EditText name=field("Nome do "+label.toLowerCase()), price=field("Preço (R$)"); price.setInputType(8194);
        add(body,name); add(body,price);
        add(body,btn("Salvar "+label.toLowerCase(),()->{
            double p=parse(price.getText().toString());
            if(name.getText().toString().trim().isEmpty()||p<0){msg("Preencha nome e preço válido.");return;}
            ContentValues cv=new ContentValues();cv.put("type",type);cv.put("name",name.getText().toString());cv.put("price",p);
            db.getWritableDatabase().insert("catalog",null,cv);catalog(type);
        }));
        section("Itens cadastrados", "Toque em um item para editar ou excluir.");
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,price FROM catalog WHERE type=? ORDER BY name",new String[]{type});
        while(c.moveToNext()){
            long id=c.getLong(0);String n=c.getString(1);double p=c.getDouble(2);
            add(body,btn(n+"   •   R$ "+money.format(p),()->editCatalog(id,n,p,type)));
        } c.close();
        add(body,btn("Voltar ao início",()->showHome()));
    }
    private void editCatalog(long id,String old,double value,String type) {
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);
        EditText n=field("Nome");n.setText(old);EditText p=field("Preço");p.setInputType(8194);p.setText(String.valueOf(value));box.addView(n);box.addView(p);
        new AlertDialog.Builder(this).setTitle("Editar cadastro").setView(box).setPositiveButton("Salvar",(d,w)->{
            ContentValues cv=new ContentValues();cv.put("name",n.getText().toString());cv.put("price",parse(p.getText().toString()));db.getWritableDatabase().update("catalog",cv,"id=?",new String[]{String.valueOf(id)});catalog(type);
        }).setNegativeButton("Excluir",(d,w)->{db.getWritableDatabase().delete("catalog","id=?",new String[]{String.valueOf(id)});catalog(type);}).setNeutralButton("Cancelar",null).show();
    }
    private void newQuote() {
        base("Novo orçamento");
        section("Dados do orçamento", "Informe o cliente, o serviço e os valores.");
        EditText client=field("Nome do cliente"), description=field("Descrição do serviço");
        EditText materials=field("Materiais (descrição e quantidades)"), mat=field("Total de materiais (R$)");
        EditText labor=field("Mão de obra (descrição)"), laborValue=field("Total da mão de obra (R$)"), discount=field("Desconto (R$)");
        for(EditText e:new EditText[]{client,description,materials,mat,labor,laborValue,discount}) { if(e==materials||e==labor)e.setSingleLine(false); if(e==mat||e==laborValue||e==discount)e.setInputType(8194); add(body,e); }
        TextView note=text("Materiais e mão de obra ficam separados no PDF. Os valores deste orçamento podem ser ajustados sem alterar o catálogo.",12);
        note.setTextColor(muted); add(body,note);
        add(body,btn("Calcular e salvar orçamento",()->{
            if(client.getText().toString().trim().isEmpty()){msg("Informe o cliente.");return;}
            double m=parse(mat.getText().toString()), l=parse(laborValue.getText().toString()), d=parse(discount.getText().toString());
            if(m<0||l<0||d<0||d>m+l){msg("Confira os valores e o desconto.");return;}
            ContentValues cv=new ContentValues();cv.put("client",client.getText().toString().trim());cv.put("description",description.getText().toString());
            cv.put("materials",materials.getText().toString());cv.put("material_value",m);cv.put("labor",labor.getText().toString());cv.put("labor_value",l);cv.put("discount",d);cv.put("total",m+l-d);cv.put("created",System.currentTimeMillis());
            long id=db.getWritableDatabase().insert("quotes",null,cv);msg("Orçamento salvo. Total: R$ "+money.format(m+l-d)); quoteActions(id);
        }));
        add(body,btn("Cancelar",()->showHome()));
    }
    private void quoteActions(long id) {
        new AlertDialog.Builder(this).setTitle("Orçamento salvo").setMessage("Deseja gerar o PDF agora?")
          .setPositiveButton("Gerar e compartilhar",(d,w)->makePdf(id,true)).setNegativeButton("Depois", (d,w)->showHome()).show();
    }
    private void history() {
        base("Histórico");
        section("Orçamentos salvos", "Toque em um orçamento para gerar e compartilhar o PDF.");
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,client,total,created FROM quotes ORDER BY created DESC",null);
        if(c.getCount()==0)add(body,text("Nenhum orçamento salvo ainda.",15));
        while(c.moveToNext()){
            long id=c.getLong(0);String name=c.getString(1);double total=c.getDouble(2);String date=new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","BR")).format(new Date(c.getLong(3)));
            add(body,btn(name+"  •  "+date+"\nTotal: R$ "+money.format(total),()->makePdf(id,true)));
        } c.close(); add(body,btn("Voltar ao início",()->showHome()));
    }
    private String safeFileName(String value) {
        String normalized = java.text.Normalizer.normalize(value == null ? "" : value, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}+", "").replaceAll("[^a-zA-Z0-9]+", "-");
        normalized = normalized.replaceAll("^-+|-+$", "");
        return normalized.isEmpty() ? "cliente" : normalized;
    }
    private void makePdf(long id,boolean share) {
        Cursor c=db.getReadableDatabase().rawQuery("SELECT client,description,materials,material_value,labor,labor_value,discount,total,created FROM quotes WHERE id=?",new String[]{String.valueOf(id)});
        if(!c.moveToFirst()){c.close();return;}
        String client=c.getString(0),desc=c.getString(1),materials=c.getString(2);double mv=c.getDouble(3);String labor=c.getString(4);double lv=c.getDouble(5),disc=c.getDouble(6),total=c.getDouble(7);long created=c.getLong(8);c.close();
        PdfDocument pdf=new PdfDocument();PdfDocument.PageInfo info=new PdfDocument.PageInfo.Builder(595,842,1).create();PdfDocument.Page page=pdf.startPage(info);Canvas canvas=page.getCanvas();Paint p=new Paint(3);
        p.setColor(navy);canvas.drawRect(0,0,595,125,p);p.setColor(Color.WHITE);p.setTextSize(25);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));canvas.drawText("MK INSTALAÇÕES ELÉTRICAS",36,55,p);p.setTextSize(14);p.setTypeface(Typeface.DEFAULT);canvas.drawText("Telefone/WhatsApp: 54 99674-7442",36,85,p);
        p.setColor(Color.BLACK);p.setTextSize(18);p.setTypeface(Typeface.DEFAULT_BOLD);canvas.drawText("ORÇAMENTO",36,165,p);p.setTextSize(12);p.setTypeface(Typeface.DEFAULT);
        int y=195; y=line(canvas,p,"Data: "+new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","BR")).format(new Date(created)),y);
        y=line(canvas,p,"Cliente: "+client,y);y=line(canvas,p,"Serviço: "+desc,y+5);y+=10;
        p.setTypeface(Typeface.DEFAULT_BOLD);y=line(canvas,p,"MATERIAIS",y);p.setTypeface(Typeface.DEFAULT);y=line(canvas,p,materials,y);y=line(canvas,p,"Subtotal de materiais: R$ "+money.format(mv),y+4);y+=14;
        p.setTypeface(Typeface.DEFAULT_BOLD);y=line(canvas,p,"MÃO DE OBRA",y);p.setTypeface(Typeface.DEFAULT);y=line(canvas,p,labor,y);y=line(canvas,p,"Subtotal de mão de obra: R$ "+money.format(lv),y+4);y+=18;
        y=line(canvas,p,"Desconto: R$ "+money.format(disc),y);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(20);y+=18;canvas.drawText("TOTAL: R$ "+money.format(total),36,y,p);
        p.setTypeface(Typeface.DEFAULT);p.setTextSize(10);canvas.drawText("Orçamento sujeito à aprovação. Obrigado pela preferência!",36,790,p);
        pdf.finishPage(page);
        try {
            File dir=new File(getExternalFilesDir(null),"orcamentos");if(!dir.exists())dir.mkdirs();
            String fileName="orcamento-"+safeFileName(client)+"-"+id+".pdf";
            File file=new File(dir,fileName);FileOutputStream out=new FileOutputStream(file);pdf.writeTo(out);out.close();pdf.close();
            if(share){Uri uri=FileProvider.getUriForFile(this,getPackageName()+".provider",file);Intent in=new Intent(Intent.ACTION_SEND);in.setType("application/pdf");in.putExtra(Intent.EXTRA_STREAM,uri);in.putExtra(Intent.EXTRA_SUBJECT,"Orçamento - "+client);in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(in,"Compartilhar orçamento PDF"));}
            else msg("PDF salvo: "+fileName);
        } catch(Exception e){pdf.close();msg("Erro ao gerar PDF: "+e.getMessage());}
    }
    private int line(Canvas c,Paint p,String s,int y){p.setTextSize(13);String value=s==null?"":s;int max=82;while(value.length()>max){c.drawText(value.substring(0,max),36,y,p);value=value.substring(max);y+=19;}c.drawText(value,36,y,p);return y+22;}
    private double parse(String s){try{return Double.parseDouble(s.trim().replace(".","").replace(",","."));}catch(Exception e){return 0;}}
    private void msg(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
    @Override public void onBackPressed(){showHome();}
    static class DB extends SQLiteOpenHelper {
        DB(Context c){super(c,"mk_instalacoes.db",null,1);}
        public void onCreate(SQLiteDatabase d){
            d.execSQL("CREATE TABLE clients(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,phone TEXT,address TEXT)");
            d.execSQL("CREATE TABLE catalog(id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT,name TEXT,price REAL)");
            d.execSQL("CREATE TABLE quotes(id INTEGER PRIMARY KEY AUTOINCREMENT,client TEXT,description TEXT,materials TEXT,material_value REAL,labor TEXT,labor_value REAL,discount REAL,total REAL,created INTEGER)");
        }
        public void onUpgrade(SQLiteDatabase d,int o,int n){}
    }
}
