/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganoapp;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;

        
/**
 *
 * @author Elcot
 */
public class SteganoApp extends JFrame implements ActionListener
{

    /**
     * @param args the command line arguments
     */
    public JLabel LFileName,LMessage,LSudoku;
    
    public JButton Encrypt,Send,View;
    public JTextField TFileName,TMessage,TSudoku;
    
    public JPanel PFileName,PMessage,PSudoku,PButtons;
    
    public int[][] M=new int[27][27];
    public int[][] T=new int[9][9];
    
    public int[] RGBArray=new int[786440];
    public int[] ModifiedRGBArray=new int[786440];
    
    public String charset=" !\"#$%&\'()*+,-./0123456789:;<=>?@";
    public String FileURL="C:\\";
    public String SudokuURL=FileURL;
            
    
    public int Width;
    public int Height;
    
    public int[] CH=new int[9];
    public int[] CV=new int[9];
    public int[][] CB=new int[3][3];
    public int[] X=new int[3];
    public int[] Y=new int[3];
    
    public BufferedImage Original,Modified;
    
    public static JFrame mainframe;
    
    public SteganoApp() throws Exception
    {
        super("SteganoApp-Sender");
        LFileName=new JLabel("File Url:");
        LMessage=new JLabel("Secret Message:");
        LSudoku=new JLabel("Sudoku Url:");
        
        Encrypt=new JButton("Encrypt");
        Send=new JButton("Send");
        View=new JButton("View");
        
        TFileName=new JTextField("",40);
        TMessage=new JTextField("",40);
        TSudoku=new JTextField("",40);
        
        PFileName=new JPanel();
        PMessage=new JPanel();
        PSudoku=new JPanel();
        PButtons=new JPanel();
        
        Container container=getContentPane();
        BoxLayout layout=new BoxLayout(container,BoxLayout.Y_AXIS);
        container.setLayout(layout);
        
        
        
        PFileName.add(LFileName);
        PFileName.add(TFileName);
        PMessage.add(LMessage);
        PMessage.add(TMessage);
        PSudoku.add(LSudoku);
        PSudoku.add(TSudoku);
        PButtons.add(Encrypt);
        PButtons.add(View);
        PButtons.add(Send);
        
        container.add(PFileName);
        container.add(PMessage);
        container.add(PSudoku);
        container.add(PButtons);
        
        Send.addActionListener(this);
        Encrypt.addActionListener(this); 
        View.addActionListener(this);
    }
    
    public static void main(String[] args) throws Exception
    { 
        SteganoApp frame=new SteganoApp();
        frame.setSize(800,600);
        frame.setVisible(true);

        mainframe=frame;    
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
            	System.exit(0);
            }
        });   
    }

    public String reverse(String str) 
    {
    	return new StringBuilder(str).reverse().toString();
    }

    public int findIndex(char sc)
    {
        for(int i=0;i<charset.length();i++)
        {
            if(sc==charset.charAt(i))
                return i;
        }
        return -1;
    }

    public void findCandidates(int R,int G,int Si)
    {
        int g1,g2;
        g1=(R%9)+9;
        g2=(G%9)+9;
        int cnt=0;
        if(G>3 && G<252)
        {
            int k=0;
            for(int i=g2-4;i<=g2+4;i++)
            {
                CH[k]=M[g1][i];
                if(CH[k]==Si)
                {
	                X[cnt]=g1;
	                Y[cnt]=i;
	                cnt+=1;
                }
                k++;
            }
        } 
        else if(G<=3)
        {
            int k=0;
            for(int i=9;i<=17;i++)
            {    
                CH[k]=M[g1][i];
                if(CH[k]==Si)
                {
	                X[cnt]=g1;
	                Y[cnt]=i;
	                cnt+=1;
	        }
                k++;
            }    
        }
        else
        {
            int k=0;
            for(int i=4;i<=12;i++)
            {    
                CH[k]=M[g1][i];
                if(CH[k]==Si)
                {
	                X[cnt]=g1;
	                Y[cnt]=i;
	                cnt+=1;
                }
                k++;
            }    
        }    
        if(R>3 && R<252)
        {
            int k=0;
            for(int i=g1-4;i<=g1+4;i++)
            {
                CV[k]=M[i][g2];
                if(CV[k]==Si)
                {
	                X[cnt]=i;
	                Y[cnt]=g2;
	                cnt+=1;
                }
                k++;
            }
        } 
        else if(R<=3)
        {
            int k=0;
            for(int i=9;i<=17;i++)
            {    
                CV[k]=M[i][g2];
                if(CV[k]==Si)
                {
	                X[cnt]=i;
	                Y[cnt]=g2;
	                cnt+=1;
                }
                k++;
            }   
        }
        else
        {
            int k=0;
            for(int i=4;i<=12;i++)
            {    
                CV[k]=M[i][g2];
                if(CV[k]==Si)
                {
	                X[cnt]=i;
	                Y[cnt]=g2;
	                cnt+=1;
                }
                k++;
            }    
        }       
        if(R<252 && G<255)
        {
            int xb=(g1/3)*3;
            int yb=(g2/3)*3;
            int r,c;
            r=0;
            for(int i=xb;i<=xb+2;i++)
            {                 
                c=0;
                for(int j=yb;j<=yb+2;j++)
                    CB[r][c++]=M[i][j];
                r++;
            }    
        }   
        else
        {
            for(int i=0;i<3;i++)
                for(int j=0;j<3;j++)
                    CB[i][j]=9;
        }
    }

    public int manhattan(int x1,int y1,int x2,int y2)
    {
        return Math.abs(x1-x2)+Math.abs(y1-y2);
    }        
    
    public int[] findMinDistortion(int R,int G,int Si)
    {
        int g1,g2;
        g1=(R%9)+9;
        g2=(G%9)+9;
        
        int k=2;        
        
        if(R<252 && G<255)
        {
            int xb=(g1/3)*3;
            int yb=(g2/3)*3;
            
            for(int i=0;i<3;i++)
            {    
                for(int j=0;j<3;j++)
                    if(CB[i][j]==Si)
                    {
                        X[k]=xb+i;
                        Y[k]=yb+j;
                        k++;
                        break;
                    }    
            }
        }    
        int[] ret=new int[2];
        int idx=0;
        int mn=(1<<31);
        for(int i=0;i<k;i++)
        {
            int cur;
            if((cur=manhattan(X[i],Y[i],g1,g2))<mn)
            {
                idx=i;
                mn=cur;
            }    
        }
        ret[0]=X[idx];
        ret[1]=Y[idx];
        return ret;    
    }        

     public void getSudoku() throws Exception
     {
         FileInputStream fis=new FileInputStream(SudokuURL);
         BufferedReader br=new BufferedReader(new InputStreamReader(fis));
         String st;
         int r,c;
         r=0;         
         while((st=br.readLine())!=null)
         {
             c=0;
             String[] tokens=st.split(" ");
             for(int i=0;i<tokens.length;i++)
                  T[r][c++]=Integer.parseInt(tokens[i]);
             r++;
         }
         for(int i=0;i<27;i++)
             for(int j=0;j<27;j++)
                 M[i][j]=T[i%9][j%9]-1;
     }
             
     public void getImageFile() throws Exception
     {
         BufferedImage img = null;
         img = ImageIO.read(new File(FileURL));
         Width=img.getWidth();
         Height=img.getHeight();

         img.getRGB(0,0,Width,Height,RGBArray,0,Width);        
     }  

     public String normalize(String st) throws Exception
     {
         st=st.trim();
         st=st.toLowerCase();         
         return st;         
     }

     public String ConvertBase9(String st) throws Exception
     {
        String ret="";
        int[] Ascii=new int[st.length()+1];
        for(int i=0;i<st.length();i++)
        {
            if(Character.isLetter(st.charAt(i)))
                Ascii[i]=(int)st.charAt(i);
            else
            {
                int idx=findIndex(st.charAt(i));
                Ascii[i]=123+idx;
            }    
        }    
        
        for(int i=0;i<st.length();i++)
        {
            int x=Ascii[i];
            String cur="";
            char c;
            while(x>8)
            {
                c=(char)((x%9)+'0');
                cur+=c;
                x/=9;               
            }
            c=(char)(x+'0');
            cur+=c;
            cur=reverse(cur);
            
            ret+=cur;
        }
        
        return ret;         
     }
     
     public void createView() throws Exception
     {
         
         JFrame frame=new JFrame("Original and modified images");
         frame.setSize(800,600);
         frame.setVisible(true);
         
         Container container=frame.getContentPane();
        container.setLayout(new FlowLayout());
        
        JLabel Lorg = new JLabel(new ImageIcon(Original));
        JLabel Lmod = new JLabel(new ImageIcon(Modified));
        JPanel PImages = new JPanel();
        PImages.add(Lorg);
        PImages.add(Lmod);
        container.add(PImages);
         
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
                mainframe.setVisible(true);
            }
        });
     }
     
     public void encode(String digits) throws Exception
     {
        int cnt=0;
        for(int i=0;i<RGBArray.length;i++)
        {
             
             int R = (RGBArray[i] >> 16) & 0xff;     
             int G = (RGBArray[i] >> 8) & 0xff;             
             int B = (RGBArray[i]) & 0xff;
             int newR,newG;
             newR=newG=0;
             if(i<digits.length())
             {
                 int d=digits.charAt(i)-'0';
                 findCandidates(R,G,d);                 
                 
                 int[] newValues=findMinDistortion(R,G,d);
                 newR=newValues[0];
                 newG=newValues[1];
                 
                 ModifiedRGBArray[i] = (newR << 16) | (newG << 8) | B;
             }
             else if(i==digits.length())
                  ModifiedRGBArray[i] = (255 << 16) | (255 << 8) | 255;
             else
                 ModifiedRGBArray[i] = (R << 16) | (G << 8) | B;  
        }
     }
     
     public void sendMessage() throws Exception
     {
            ServerSocket ss=new ServerSocket(3563);
            Socket s=ss.accept();
            ObjectOutputStream out=new ObjectOutputStream(s.getOutputStream());
            for(int i=0;i<ModifiedRGBArray.length;i++)
            {
                out.writeInt(ModifiedRGBArray[i]);
            }
            out.close();
            s.close();
            ss.close();
	    //connection=server.accept();
     }
     
    @Override
    public void actionPerformed(ActionEvent e)
    {
        
        if(e.getSource()==Encrypt)
        {
            try
            {
                    SudokuURL+=TSudoku.getText();
	            FileURL+=TFileName.getText();
	            getSudoku();
	            getImageFile();
	            
	            String S=normalize(TMessage.getText());
	            S=ConvertBase9(S);
	            encode(S);
            
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }     
        }    
        else if(e.getSource()==View)
        {
            try
            {
                
	            Original=new BufferedImage(Width,Height,BufferedImage.TYPE_3BYTE_BGR);
	            Original.setRGB(0, 0, Width, Height, RGBArray, 0, Width);
	            
	            Modified=new BufferedImage(Width,Height,BufferedImage.TYPE_3BYTE_BGR);
	            Modified.setRGB(0, 0, Width, Height, ModifiedRGBArray, 0, Width);            
            
            	    createView();
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        else if(e.getSource()==Send)
        {
            try
            {
            	sendMessage();
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        
   }    
}