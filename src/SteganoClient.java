/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package steganoapp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 *
 * @author Elcot
 */
public class SteganoClient 
{
    int[] RGBArray;
    int[][] Sudoku;
    int[][] M;
    int[] pw9;
    String sudokuURL="C:\\sudoku1.txt";
    String charset=" !\"#$%&\'()*+,-./0123456789:;<=>?@";
    
    public SteganoClient()
    {
        pw9=new int[4];
        pw9[0]=1;
        pw9[1]=9;
        pw9[2]=81;
        pw9[3]=729;
    }
    
    public static void main(String args []) throws FileNotFoundException, IOException,Exception
    {
        SteganoClient sg=new SteganoClient();
        
        sg.readSudoku();
        sg.getMessage();
        String msg=sg.decodeMessage();
        System.out.println(msg);
    }
    
    public void readSudoku() throws FileNotFoundException, IOException
    {
        Sudoku=new int[9][9];
        M=new int[27][27];
        FileInputStream fis=new FileInputStream(sudokuURL);
        BufferedReader br=new BufferedReader(new InputStreamReader(fis));
        String line;
        int r,c;
        r=c=0;        
        while((line=br.readLine())!=null)
        {
            System.out.println(line);
            String[] tokens=line.split(" ");
            c=0;
            for(int i=0;i<tokens.length;i++)
            {
                Sudoku[r][c++]=Integer.parseInt(tokens[i]);
            }
            r++;            
        }
        for(int i=0;i<27;i++)
            for(int j=0;j<27;j++)
                M[i][j]=Sudoku[i%9][j%9]-1;
    }
    
    public void getMessage()
    {
        RGBArray=new int[786440];    
        int k=0;
        try
        {
            Socket s=new Socket("localhost",3563);
            InputStream is=s.getInputStream();
            ObjectInputStream in=new ObjectInputStream(is);
            while(true)
            {
                try
                {
                    RGBArray[k++]=in.readInt();
                }
                catch(EOFException e)
                {
                    in.close();
                    break;
                }            
            }
        }    
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public int convertToDecimal(String base9) throws Exception
    {
        int ret=0;
        for(int i=0;i<base9.length();i++)
        {
            ret+=(base9.charAt(i)-'0')*pw9[2-i];
        }
        return ret;
    }
    
    public String decodeMessage() throws Exception
    {
        String ret="";
        for(int i=0;i<RGBArray.length;i++)
        {
             int R = (RGBArray[i] >> 16) & 0xff;     
             int G = (RGBArray[i] >> 8) & 0xff;
             int B = (RGBArray[i]) & 0xff;
             if(R==255 && G==255 && B==255)
                 break;
             
             R=R % 9;
             G=G % 9;
             int digit=M[R][G];
             ret+=""+digit+"";
        }
        
        String[] tokens=new String[500];
        int k=0;
        for(int i=0;i<ret.length();)
        {
            tokens[k++]=ret.substring(i,i+3);
            i+=3;
        }
        int[] values=new int[500];
       
        for(int i=0;i<k;i++)
        {
            values[i]=convertToDecimal(tokens[i]);
        }
        ret="";
        for(int i=0;i<k;i++)
        {
            char c;
            if(values[i]<=122)
                c=(char)values[i];
            else
                c=(char)(values[i]-91);
            ret+=c;
        }
        return ret;
    }
}
