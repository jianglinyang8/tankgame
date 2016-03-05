package tankgame;

import java.util.Vector;
import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

//������������
class AePlayWave extends Thread {

	private String filename;
	public AePlayWave(String wavfile) {
		filename = wavfile;

	}

	public void run() {

		File soundFile = new File(filename);

		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine auline = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		auline.start();
		int nBytesRead = 0;
		byte[] abData = new byte[512];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			auline.drain();
			auline.close();
		}
	}	
}

class node
{
	int x;
	int y;
	int direct;
	public node(int x,int y,int direct)
	{
		this.x=x;
		this.y=y;
		this.direct=direct;
	}
}
//��¼�࣬ͬʱҲ���Ա�����ҵ�����
class recorder
{
	//��¼ÿ���ж��ٵ���
	private static int ennum=20;
	//�������ж��ٿ����õ���
	private static int mylife=3;
	//��¼�ܹ������˶��ٵ���
	private static int allennum=0;
	//���ļ��лָ���¼��
	public static Vector<node> nodes = new Vector<node>();
	
	private static FileWriter fw=null;
	private static BufferedWriter bw=null;
	private static FileReader fr=null;
	private static BufferedReader br=null;
	
	private static  Vector<enemytank> ets=new Vector<enemytank>();
	public static Vector<enemytank> getEts() {
		return ets;
	}
	public static void setEts(Vector<enemytank> etss) {
		ets = etss;
	}
	//��ɶ�ȡ����
	public static void getnodesandennums()
	{
		try {
			fr=new FileReader("f:\\myrecording.txt");
			br=new BufferedReader(fr);
			//�ȶ�ȡ��һ��
			String n=br.readLine();
			allennum=Integer.parseInt(n);
			//�ٽ��Ŷ�
			while((n=br.readLine())!=null)
			{
				String xyz[]=n.split(" ");
			
				node nd=new node(Integer.parseInt(xyz[0]),Integer.parseInt(xyz[1]),Integer.parseInt(xyz[2]));
				nodes.add(nd);
			}
		
		} catch (Exception e) {
			// TODO: handle exception
		}finally
		{
			try {
				br.close();
				fr.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
	}
	//������ٵ��������͵���̹�����꣬����
	public static void keeprecandenemytank()
	{
		try {
			//����
			fw=new FileWriter("f:\\myrecording.txt");
			bw=new BufferedWriter(fw);
			bw.write(allennum+"\r\n");
			//���浱ǰ��ĵ���̹�˵�����ͷ���
			for(int i=0;i<ets.size();i++)
			{
				//ȡ����һ��̹��
				enemytank et=ets.get(i);
				if(et.isLive)
				{
					//��ı���
					String recode=et.x+" "+et.y+" "+et.direct;
					
					//д��
					bw.write(recode+"\r\n");
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally
		{
			try {
				bw.close();
				fw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	//���ļ��ж�ȡ����¼
	public static void getrecorder()
	{
		try {
			fr=new FileReader("f:\\myrecording.txt");
			br=new BufferedReader(fr);
			String n=br.readLine();
			allennum=Integer.parseInt(n);
		} catch (Exception e) {
			// TODO: handle exception
		}finally
		{
			try {
				br.close();
				fr.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
	}
	//����һ��ٵ���̹���������浽�ļ���
	public static void keeprecorder()
	{
		try {
			//����
			fw=new FileWriter("f:\\myrecording.txt");
			bw=new BufferedWriter(fw);
			bw.write(allennum+"\r\n");
		} catch (Exception e) {
			// TODO: handle exception
		}finally
		{
			try {
				bw.close();
				fw.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	public static int getAllennum() {
		return allennum;
	}
	public static void setAllennum(int allennum) {
		recorder.allennum = allennum;
	}
	public static int getEnnum() {
		return ennum;
	}
	public static void setEnnum(int ennum) {
		recorder.ennum = ennum;
	}
	public static int getMylife() {
		return mylife;
	}
	public static void setMylife(int mylife) {
		recorder.mylife = mylife;
	}
	//���ٵ�������
	public  static void reduceennum()
	{
		ennum--;
	}
	//�����Լ�����
	public static void reducelife()
	{
		mylife--;
	}
	//�����ҵ�ս����
	public static void addallennum()
	{
		allennum++;
	}
}
//ը����
class bomb
{
	//����ը��������
	int x,y;
	//ը��������
	int life=9; 
	boolean islive=true;
	public bomb(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	//��������ֵ
	public void lifedown()
	{
		if(life>0)
		{
			try {
				Thread.sleep(30);
			} catch (Exception e) {
				// TODO: handle exception
			}
			life--;
		}else{
			this.islive=false;
		}
	}
}
//�ӵ���  �߳�
class shot implements Runnable
{
	int x;
	int y;
	int direct;
	int speed=1;
	//�ӵ��Ƿ񻹻���
	boolean isLive=true;
	
	public shot(int x,int y,int direct)
	{
		this.x=x;
		this.y=y;
		this.direct=direct;
	}
	public void run()
	{
		while(true)
		{
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO: handle exception
			}
			switch(direct)
			{
			case 0:
				//��
				y-=speed;
				break;
			case 1:
				x+=speed;
				break;
			case 2:
				y+=speed;
				break;
			case 3:
				x-=speed;
				break;
			}
			//System.out.println("�ӵ�����x="+x+"y="+y);
			//�ӵ���ʱ������������
			
			//�жϸ��ӵ��Ƿ�������Ե
			if(x<0||x>400||y<0||y>300)
			{
				this.isLive=false;
				break;
			}
		}
	}
}
//̹����
class Tank
{
	//��ʾ̹�˵ĺ�����
	int x=0;
	//̹�˵�������
	int y=0;
	
	//̹�˷���
	//0 ��ʾ�� 1��ʾ�� 2��ʾ�� 3��ʾ��
	int direct=0;
	//̹�˵��ٶ�
	int speed=1;
	//��ɫ
	int color;
	boolean isLive=true;
	
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Tank(int x, int y)
	{
		this.x=x;
		this.y=y; 
	}
	
}
//���˵�̹��,�ѵ���̹�������߳���
class enemytank extends Tank implements Runnable
{
	//boolean isLive=true;
	int flagdir=1;
	int times=0;
	//����һ�����������Է��ʵ�mypanel�����е��˵�̹��
	Vector<enemytank> ets=new Vector<enemytank>();
	//����һ�����������Դ�ŵ��˵��ӵ�
	Vector<shot> ss=new Vector<shot>();
	//��������ӵ���Ӧ���ڸոմ���̹�˺͵��˵�̹���ӵ�������
	
	public enemytank(int x,int y)
	{
		super(x,y);
	}
	
	//�õ�mypanel�ĵ���̹������
	public void setets(Vector<enemytank> vv)
	{
		this.ets=vv;
	}
	//�ж��Ƿ������˱�ĵ��˵�̹��
	public boolean istouchotherenenmy()
	{
		boolean b=false;
		switch(this.direct)
		{
		case 0:
			//�ҵ�̹������
			//ȡ�����еĵ���̹��
			for(int i=0;i<ets.size();i++)
			{
				//ȡ����һ��̹��
				enemytank et=ets.get(i);
				//��������Լ�
				if(et!=this)
				{
					//�����ĵ��˵ķ��������»�������
					if(et.direct==0||et.direct==2)
					{
						if(this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true; 
						}
					}
					if(et.direct==3||et.direct==1)
					{
						if(this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true; 
						}
					}
				}
			}
			break;
		case 1:
			//̹������
			//ȡ�����еĵ���̹��
			for(int i=0;i<ets.size();i++)
			{
				//ȡ����һ��̹��
				enemytank et=ets.get(i);
				//��������Լ�
				if(et!=this)
				{
					//�����ĵ��˵ķ��������»�������
					if(et.direct==0||et.direct==2)
					{
						if(this.x+30>=et.x&&this.x+30<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
						if(this.x+30>=et.x&&this.x+30<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
						{
							return true; 
						}
					}
					if(et.direct==3||et.direct==1)
					{
						if(this.x+30>=et.x&&this.x+30<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
						if(this.x+30>=et.x&&this.x+30<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20)
						{
							return true; 
						}
					}
				}
			}
			break;
		case 2:
			//̹������
			//ȡ�����еĵ���̹��
			for(int i=0;i<ets.size();i++)
			{
				//ȡ����һ��̹��
				enemytank et=ets.get(i);
				//��������Լ�
				if(et!=this)
				{
					//�����ĵ��˵ķ��������»�������
					if(et.direct==0||et.direct==2)
					{
						if(this.x>=et.x&&this.x<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+20&&this.y+30>=et.y&&this.y+30<=et.y+30)
						{
							return true; 
						}
					}
					if(et.direct==3||et.direct==1)
					{
						if(this.x>=et.x&&this.x<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20)
						{
							return true;
						}
						if(this.x+20>=et.x&&this.x+20<=et.x+30&&this.y+30>=et.y&&this.y+30<=et.y+20)
						{
							return true; 
						}
					}
				}
			}
			break;
		case 3:
			//����
			//ȡ�����еĵ���̹��
			for(int i=0;i<ets.size();i++)
			{
				//ȡ����һ��̹��
				enemytank et=ets.get(i);
				//��������Լ�
				if(et!=this)
				{
					//�����ĵ��˵ķ��������»�������
					if(et.direct==0||et.direct==2)
					{
						if(this.x>=et.x&&this.x<=et.x+20&&this.y>=et.y&&this.y<=et.y+30)
						{
							return true;
						}
						if(this.x>=et.x&&this.x<=et.x+20&&this.y+20>=et.y&&this.y+20<=et.y+30)
						{
							return true; 
						}
					}
					if(et.direct==3||et.direct==1)
					{
						if(this.x>=et.x&&this.x<=et.x+30&&this.y>=et.y&&this.y<=et.y+20)
						{
							return true;
						}
						if(this.x>=et.x&&this.x<=et.x+30&&this.y+20>=et.y&&this.y+20<=et.y+20)
						{
							return true; 
						}
					}
				}
			}
			break;
		}
		
		return b;
	}
	public void run()
	{
		while(true)
		{		
			switch(this.direct)
			{
			case 0:
				//˵��̹�����������ƶ�
				for(int i=0;i<30;i++)
				{
					if(y>0&&!this.istouchotherenenmy())
					{
						y-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				break;
			case 1:
				//����
				for(int i=0;i<30;i++)
				{
					if(x<370&&!this.istouchotherenenmy())
					{
						x+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				break;
			case 2:
				//����
				for(int i=0;i<30;i++)
				{
					if(y<270&&!this.istouchotherenenmy())
					{
						y+=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				break;
			case 3:
				//����
				for(int i=0;i<30;i++)
				{
					if(x>0&&!this.istouchotherenenmy())
					{
						x-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				break;	
			}
			//this.times++;
			//if(times%2==0)
			//{	
			//	times=0;
				//�ж��Ƿ���Ҫ��̹�˼����µ��ӵ�
						
			if(flagdir==1)
			{
				if(isLive)
				{
					if(ss.size()<5)
					{
						shot s=null;
						//û���ӵ�
						//���
						switch(direct)
						{
						case 0:
							s=new shot(x+10,y,0);
							ss.add(s);
							break;
						case 1:
							s=new shot(x+30,y+10,1);
							ss.add(s);
							break;
						case 2:
							s=new shot(x+10,y+30,2);
							ss.add(s);
							break;
						case 3:
							s=new shot(x,y+10,3);
							ss.add(s);
							break;	
						}
					
						//�����ӵ�
						Thread t=new Thread(s);
						t.start();
					}
				}			
		//	}
			
				//��̹���������һ���µķ���
				this.direct=(int)(Math.random()*4);
			}
	
			//�жϵ���̹���Ƿ�����
			if(isLive==false)
			{
				//��̹���������Ƴ��߳�                 Ҫ��Ȼ���ǽ�ʬ����
				break;
			}
		}
	}
}

//�ҵ�̹��
class Hero extends Tank
{
	//�ӵ�
	shot s=null;
	Vector<shot> ss=new Vector<shot>();
	
	Hero(int x,int y)
	{
		super(x,y);
	}
	//����
	public void shotenemy()
	{
		switch(this.direct)
		{
		case 0:
			s=new shot(x+10,y,0);
			ss.add(s);
			break;
		case 1:
			s=new shot(x+30,y+10,1);
			ss.add(s);
			break;
		case 2:
			s=new shot(x+10,y+30,2);
			ss.add(s);
			break;
		case 3:
			s=new shot(x,y+10,3);
			ss.add(s);
			break;	
		}
		//�����ӵ��߳�
		Thread t=new Thread(s);
		t.start();
	}
	//̹�������ƶ�
	public void moveUp()
	{
		if(this.y>0)
		{
			y-=speed;
		}
	}
	
	//̹�������ƶ�
	public void moveRight()
	{
		if(this.x<370)
		{
			x+=speed;
		}
	}
	//̹�������ƶ�
	public void moveDown()
	{
		if(this.y<270)
		{
			y+=speed;
		}
	}
	//̹�������ƶ�
	public void moveLeft()
	{
		if(this.x>0)
		{
			x-=speed;
		}
	}
}