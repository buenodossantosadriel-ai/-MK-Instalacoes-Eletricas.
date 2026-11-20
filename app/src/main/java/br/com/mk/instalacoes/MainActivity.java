package br.com.mk.instalacoes;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.core.content.FileProvider;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    private DB db;
    private LinearLayout root, body;
    private final int navy = Color.rgb(18, 43, 70);
    private final int blue = Color.rgb(24, 112, 174);
    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        db = new DB(this);
        showHome();
    }

    private void base(String title) {
        root = new LinearLayout(this); root.setOrientation(1); root.setBackgroundColor(Color.rgb(246,248,251));
        TextView bar = new TextView(this); bar.setText(title); bar.setTextColor(Color.WHITE); bar.setTextSize(21);
        bar.setTypeface(null,1); bar.setPadding(20,22,16,22); bar.setBackgroundColor(navy); root.addView(bar);
        ScrollView scroll = new ScrollView(this); body = new LinearLayout(this); body.setPadding(16,16,16,28); body.setOrientation(1);
        scroll.addView(body); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
    private TextView text(String s,int size) { TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.rgb(35,48,62)); t.setPadding(4,8,4,8); return t; }
    private EditText field(String hint) { EditText e=new EditText(this); e.setSingleLine(true); e.setHint(hint); e.setTextSize(16); return e; }
    private Button btn(String s,Runnable r) { Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setOnClickListener(v->r.run()); return b; }
    private void add(LinearLayout l, View v) { l.addView(v,new LinearLayout.LayoutParams(-1,-2)); }
    private void showHome() {
        base("MK Instalações Elétricas");
        add(body,text("Gestão de clientes, materiais, serviços e orçamentos.",16));
        add(body,btn("＋ Novo orçamento",()->newQuote()));
        add(body,btn("Clientes",()->clients()));
        add(body,btn("Materiais e preços",()->catalog("material")));
        add(body,btn("Serviços e mão de obra",()->catalog("servico")));
        add(body,btn("Histórico de orçamentos",()->history()));
        add(body,text("Contato: 54 99674-7442",14));
        add(body,text("Os dados são armazenados neste aparelho. O aplicativo funciona offline.",13));
    }
    private void clients() {
        base("Clientes");
        EditText name=field("Nome do cliente"), phone=field("Telefone"), address=field("Endereço");
        add(body,name); add(body,phone); add(body,address);
        add(body,btn("Salvar cliente",()->{
            if(name.getText().toString().trim().isEmpty()){msg("Informe o nome.");return;}
            ContentValues cv=new ContentValues(); cv.put("name",name.getText().toString()); cv.put("phone",phone.getText().toString()); cv.put("address",address.getText().toString());
            db.getWritableDatabase().insert("clients",null,cv); msg("Cliente salvo."); clients();
        }));
        add(body,text("Clientes cadastrados",18));
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,phone FROM clients ORDER BY name",null);
        while(c.moveToNext()) add(body,text(c.getString(1)+"\n"+c.getString(2),15));
        c.close(); add(body,btn("Voltar",()->showHome()));
    }
    private void catalog(String type) {
        String label=type.equals("material")?"Material":"Serviço";
        base(label+"s e preços");
        EditText name=field("Nome do "+label.toLowerCase()), price=field("Preço (R$)"); price.setInputType(8194);
        add(body,name); add(body,price);
        add(body,btn("Salvar "+label.toLowerCase(),()->{
            double p=parse(price.getText().toString());
            if(name.getText().toString().trim().isEmpty()||p<0){msg("Preencha nome e preço válido.");return;}
            ContentValues cv=new ContentValues();cv.put("type",type);cv.put("name",name.getText().toString());cv.put("price",p);
            db.getWritableDatabase().insert("catalog",null,cv);catalog(type);
        }));
        add(body,text("Toque em um item para editar o preço.",14));
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,price FROM catalog WHERE type=? ORDER BY name",new String[]{type});
        while(c.moveToNext()){
            long id=c.getLong(0);String n=c.getString(1);double p=c.getDouble(2);
            add(body,btn(n+" — R$ "+money.format(p),()->editCatalog(id,n,p,type)));
        } c.close();
        add(body,btn("Voltar",()->showHome()));
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
        EditText client=field("Nome do cliente"), description=field("Descrição do serviço");
        EditText materials=field("Materiais (descrição e quantidades)"), mat=field("Total de materiais (R$)");
        EditText labor=field("Mão de obra (descrição)"), laborValue=field("Total da mão de obra (R$)"), discount=field("Desconto (R$)");
        for(EditText e:new EditText[]{client,description,materials,mat,labor,laborValue,discount}) { if(e==materials||e==labor)e.setSingleLine(false); if(e==mat||e==laborValue||e==discount)e.setInputType(8194); add(body,e); }
        add(body,text("Materiais e mão de obra ficam separados no PDF. Você pode ajustar os valores neste orçamento sem alterar o catálogo.",13));
        add(body,btn("Calcular e salvar orçamento",()->{
            if(client.getText().toString().trim().isEmpty()){msg("Informe o cliente.");return;}
            double m=parse(mat.getText().toString()), l=parse(laborValue.getText().toString()), d=parse(discount.getText().toString());
            if(m<0||l<0||d<0||d>m+l){msg("Confira os valores e o desconto.");return;}
            ContentValues cv=new ContentValues();cv.put("client",client.getText().toString());cv.put("description",description.getText().toString());
            cv.put("materials",materials.getText().toString());cv.put("material_value",m);cv.put("labor",labor.getText().toString());cv.put("labor_value",l);cv.put("discount",d);cv.put("total",m+l-d);cv.put("created",System.currentTimeMillis());
            long id=db.getWritableDatabase().insert("quotes",null,cv);msg("Orçamento salvo. Total: R$ "+money.format(m+l-d)); quoteActions(id);
        }));
        add(body,btn("Voltar",()->showHome()));
    }
    private void quoteActions(long id) {
        new AlertDialog.Builder(this).setTitle("Orçamento salvo").setMessage("Deseja gerar o PDF agora?")
          .setPositiveButton("Gerar e compartilhar",(d,w)->makePdf(id,true)).setNegativeButton("Depois", (d,w)->showHome()).show();
    }
    private void history() {
        base("Histórico de orçamentos");
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,client,total,created FROM quotes ORDER BY created DESC",null);
        if(c.getCount()==0)add(body,text("Nenhum orçamento salvo ainda.",15));
        while(c.moveToNext()){
            long id=c.getLong(0);String name=c.getString(1);double total=c.getDouble(2);String date=new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","BR")).format(new Date(c.getLong(3)));
            add(body,btn(name+" • "+date+"\nTotal: R$ "+money.format(total),()->makePdf(id,true)));
        } c.close(); add(body,btn("Voltar",()->showHome()));
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
            File dir=new File(getExternalFilesDir(null),"orcamentos");dir.mkdirs();File file=new File(dir,"orcamento-"+id+".pdf");FileOutputStream out=new FileOutputStream(file);pdf.writeTo(out);out.close();pdf.close();
            if(share){Uri uri=FileProvider.getUriForFile(this,getPackageName()+".provider",file);Intent in=new Intent(Intent.ACTION_SEND);in.setType("application/pdf");in.putExtra(Intent.EXTRA_STREAM,uri);in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(in,"Compartilhar orçamento PDF"));}
            else msg("PDF salvo.");
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
