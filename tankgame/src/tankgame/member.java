package tankgame;

import java.util.Vector;
import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

//播放声音的类
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
//记录类，同时也可以保存玩家的设置
class recorder
{
	//记录每关有多少敌人
	private static int ennum=20;
	//设置我有多少可以用的人
	private static int mylife=3;
	//记录总共消灭了多少敌人
	private static int allennum=0;
	//从文件中恢复记录点
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
	//完成读取任务
	public static void getnodesandennums()
	{
		try {
			fr=new FileReader("f:\\myrecording.txt");
			br=new BufferedReader(fr);
			//先读取第一行
			String n=br.readLine();
			allennum=Integer.parseInt(n);
			//再接着读
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
	//保存击毁敌人数量和敌人坦克坐标，方向
	public static void keeprecandenemytank()
	{
		try {
			//创建
			fw=new FileWriter("f:\\myrecording.txt");
			bw=new BufferedWriter(fw);
			bw.write(allennum+"\r\n");
			//保存当前活的敌人坦克的坐标和方向
			for(int i=0;i<ets.size();i++)
			{
				//取出第一个坦克
				enemytank et=ets.get(i);
				if(et.isLive)
				{
					//活的保存
					String recode=et.x+" "+et.y+" "+et.direct;
					
					//写入
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
	//从文件中读取，记录
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
	//把玩家击毁敌人坦克数量保存到文件中
	public static void keeprecorder()
	{
		try {
			//创建
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
	//减少敌人数量
	public  static void reduceennum()
	{
		ennum--;
	}
	//减少自己数量
	public static void reducelife()
	{
		mylife--;
	}
	//增加我的战绩数
	public static void addallennum()
	{
		allennum++;
	}
}
//炸弹类
class bomb
{
	//定义炸弹的坐标
	int x,y;
	//炸弹的生命
	int life=9; 
	boolean islive=true;
	public bomb(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	//减少生命值
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
//子弹类  线程
class shot implements Runnable
{
	int x;
	int y;
	int direct;
	int speed=1;
	//子弹是否还活着
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
				//上
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
			//System.out.println("子弹坐标x="+x+"y="+y);
			//子弹何时死亡？？？？
			
			//判断该子弹是否碰到边缘
			if(x<0||x>400||y<0||y>300)
			{
				this.isLive=false;
				break;
			}
		}
	}
}
//坦克类
class Tank
{
	//表示坦克的横坐标
	int x=0;
	//坦克的纵坐标
	int y=0;
	
	//坦克方向
	//0 表示上 1表示右 2表示下 3表示左
	int direct=0;
	//坦克的速度
	int speed=1;
	//颜色
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
//敌人的坦克,把敌人坦克做成线程类
class enemytank extends Tank implements Runnable
{
	//boolean isLive=true;
	int flagdir=1;
	int times=0;
	//定义一个向量，可以访问到mypanel上所有敌人的坦克
	Vector<enemytank> ets=new Vector<enemytank>();
	//定义一个向量，可以存放敌人的子弹
	Vector<shot> ss=new Vector<shot>();
	//敌人添加子弹，应当在刚刚创建坦克和敌人的坦克子弹死亡后
	
	public enemytank(int x,int y)
	{
		super(x,y);
	}
	
	//得到mypanel的敌人坦克向量
	public void setets(Vector<enemytank> vv)
	{
		this.ets=vv;
	}
	//判断是否碰到了别的敌人的坦克
	public boolean istouchotherenenmy()
	{
		boolean b=false;
		switch(this.direct)
		{
		case 0:
			//我的坦克向上
			//取出所有的敌人坦克
			for(int i=0;i<ets.size();i++)
			{
				//取出第一个坦克
				enemytank et=ets.get(i);
				//如果不是自己
				if(et!=this)
				{
					//如果别的敌人的方向是向下或者向上
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
			//坦克向右
			//取出所有的敌人坦克
			for(int i=0;i<ets.size();i++)
			{
				//取出第一个坦克
				enemytank et=ets.get(i);
				//如果不是自己
				if(et!=this)
				{
					//如果别的敌人的方向是向下或者向上
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
			//坦克向下
			//取出所有的敌人坦克
			for(int i=0;i<ets.size();i++)
			{
				//取出第一个坦克
				enemytank et=ets.get(i);
				//如果不是自己
				if(et!=this)
				{
					//如果别的敌人的方向是向下或者向上
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
			//向左
			//取出所有的敌人坦克
			for(int i=0;i<ets.size();i++)
			{
				//取出第一个坦克
				enemytank et=ets.get(i);
				//如果不是自己
				if(et!=this)
				{
					//如果别的敌人的方向是向下或者向上
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
				//说明坦克正在向上移动
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
				//向右
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
				//向下
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
				//向左
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
				//判断是否需要给坦克加入新的子弹
						
			if(flagdir==1)
			{
				if(isLive)
				{
					if(ss.size()<5)
					{
						shot s=null;
						//没有子弹
						//添加
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
					
						//启动子弹
						Thread t=new Thread(s);
						t.start();
					}
				}			
		//	}
			
				//让坦克随机产生一个新的方向
				this.direct=(int)(Math.random()*4);
			}
	
			//判断敌人坦克是否死亡
			if(isLive==false)
			{
				//让坦克死亡后，推出线程                 要不然就是僵尸进程
				break;
			}
		}
	}
}

//我的坦克
class Hero extends Tank
{
	//子弹
	shot s=null;
	Vector<shot> ss=new Vector<shot>();
	
	Hero(int x,int y)
	{
		super(x,y);
	}
	//开火
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
		//启动子弹线程
		Thread t=new Thread(s);
		t.start();
	}
	//坦克向上移动
	public void moveUp()
	{
		if(this.y>0)
		{
			y-=speed;
		}
	}
	
	//坦克向右移动
	public void moveRight()
	{
		if(this.x<370)
		{
			x+=speed;
		}
	}
	//坦克向下移动
	public void moveDown()
	{
		if(this.y<270)
		{
			y+=speed;
		}
	}
	//坦克向左移动
	public void moveLeft()
	{
		if(this.x>0)
		{
			x-=speed;
		}
	}
}