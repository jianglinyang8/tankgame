package tankgame;
//1.画出坦克
//2.我的坦克可以上下移动
//3.可以发射子弹，子弹最多连发5颗
//
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.Savepoint;
import java.util.Vector;
import java.io.*;

/**
 * @author jiangly
 *
 */
public class tankgame extends JFrame implements ActionListener{
	Mypanel mp=null;
	
	//定义一个开始的面板
	mystartpanel msp=null;
	
	//作出我需要的菜单
	JMenuBar jmb =null;
	//开始游戏
	JMenu jm1=null;
	JMenuItem jmi1=null;
	//退出系统
	JMenuItem jmi2=null;
	//存盘退出
	JMenuItem jmi3=null;
	//去上一局
	JMenuItem jmi4=null;
	
	//判断是去上局还是新游戏
	public static String flag1="newgame";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		tankgame tk= new tankgame();
	}
	//构造函数
	public tankgame()
	{
//		mp=new Mypanel();
//		//启动mp线程
//		Thread t= new Thread(mp);
//		t.start();
//		this.add(mp);
//		//注册监听
//		this.addKeyListener(mp);
		
		//创建菜单及菜单选项
		jmb=new JMenuBar();
		jm1=new JMenu("游戏(G)");
		//设置助记符
		jm1.setMnemonic('G');
		jmb.add(jm1);
		jmi1=new JMenuItem("开始游戏(N)");
		jmi1.setMnemonic('N');
		jm1.add(jmi1);
		
		jmi2=new JMenuItem("退出游戏(E)");
		jmi2.setMnemonic('E');
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		jm1.add(jmi2);
		
		jmi3=new JMenuItem("存盘退出(c)");
		jmi3.setMnemonic('S');
		jmi3.addActionListener(this);
		jmi3.setActionCommand("saveexit");
		jm1.add(jmi3);
		
		jmi4=new JMenuItem("继续上局游戏(C)");
		jmi4.setMnemonic('C');
		jmi4.addActionListener(this);
		jmi4.setActionCommand("continue");
		jm1.add(jmi4);
		
		
		this.setJMenuBar(jmb);
		
		//对jmi1响应
		jmi1.addActionListener(this);
		jmi1.setActionCommand("newgame");
		
		msp=new mystartpanel();
		Thread t=new Thread(msp);
		t.start();
		this.add(msp);
		this.setSize(600,500);
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//对用户不同的点击，做不同的处理
		if(e.getActionCommand().equals("newgame"))
		{
			//创建战场面板
			mp=new Mypanel();
			//启动mp线程
			Thread t= new Thread(mp);
			t.start();
			//先删除旧的开始面板
			this.remove(msp);
			this.add(mp);
			//注册监听
			this.addKeyListener(mp);
			//显示，刷新jframe
			this.setVisible(true);
		}else if(e.getActionCommand().equals("exit"))
		{
			//用户点击了退出系统菜单
			//保存击毁敌人数量
			recorder.keeprecorder();
			System.exit(0);
		}else if(e.getActionCommand().equals("saveexit"))
		{ 
			//工作
			//保存击毁敌人的数量和敌人的坐标
			recorder.setEts(mp.ets);
			recorder.keeprecandenemytank();
			//退出
			System.exit(0);
		}else if(e.getActionCommand().equals("continue"))
		{
			recorder.getnodesandennums();
			flag1="continue";
			//创建战场面板
			mp=new Mypanel();	
			//启动mp线程
			Thread t= new Thread(mp);
			t.start();
			//先删除旧的开始面板
			this.remove(msp);
			this.add(mp);
			//注册监听
			this.addKeyListener(mp);
			//显示，刷新jframe
			this.setVisible(true);
		}
	}
}

//就是一个提示作用
class mystartpanel extends JPanel implements Runnable
{
	//闪烁效果
	int times=0;
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		times++;
		//提示信息
		if(times%2==0)
		{
			times=0;
			g.setColor(Color.yellow);
			//开关信息的字体                                               粗体
			Font myfont=new Font("华文新魏", Font.BOLD, 30);
			g.setFont(myfont);
			g.drawString("stage: 1", 150, 150);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			//休眠
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//重画
			this.repaint();
		}
	}
}

//我的面板
class Mypanel extends JPanel implements KeyListener,Runnable
{
	//暂停标志位
	int flag=0;
	int herobuff;
	int heroshotbuff;
	int enemytankbuff;
	int enemytankshotbuff;

	//定义一个我的坦克
	Hero hero=null;
	//定义敌人的坦克组
	Vector <enemytank> ets= new Vector<enemytank>();
	
	//定义炸弹集合
	Vector<bomb> bombs=new Vector<bomb>();
	//敌人坦克数量
	int ensize=3;
	
	//定义三张图片,三张图片才能组成一颗炸弹
	Image image1=null;
	Image image2=null;
	Image image3=null;
	
	//构造函数
	public Mypanel()
	{
		//恢复记录数据
		recorder.getrecorder();
		
		hero = new Hero(200,150);
		hero.speed=3;
		if(tankgame.flag1.equals("newgame"))
		{
			//初始化敌人的坦克
			for(int i=0;i<ensize;i++)
			{
				//创建一辆敌人的坦克对象
				enemytank et =new enemytank((i+1)*50, 0);
				et.setColor(0);
				et.setDirect(2);
				//将mypanel的敌人坦克向量交给该敌人坦克
				et.setets(ets);
				//启动敌人坦克
				Thread t=new Thread(et);
				t.start();
				//给敌人坦克添加一颗子弹
				shot s = new shot(et.x+10, et.y+30, 2);
				//子弹加入敌人坦克
				et.ss.add(s);
				Thread t2 =new Thread(s);
				t2.start();
				//加入
				ets.add(et);
			}
		}
		else
		{
			//初始化敌人的坦克
			for(int i=0;i<recorder.nodes.size();i++)
			{
				node nd=recorder.nodes.get(i);
				//创建一辆敌人的坦克对象
				enemytank et =new enemytank(nd.x, nd.y);
				et.setColor(0);
				et.setDirect(nd.direct);
				//将mypanel的敌人坦克向量交给该敌人坦克
				et.setets(ets);
				//启动敌人坦克
				Thread t=new Thread(et);
				t.start();
				//给敌人坦克添加一颗子弹
				shot s = new shot(et.x+10, et.y+30, 2);
				//子弹加入敌人坦克
				et.ss.add(s);
				Thread t2 =new Thread(s);
				t2.start();
				//加入
				ets.add(et);
			}
		}
		AePlayWave apw=new AePlayWave("./111.wav");
		apw.start();
//		try {
//			image1=ImageIO.read(new File("bomb_1.gif"));
//			image2=ImageIO.read(new File("bomb_2.gif"));
//			image3=ImageIO.read(new File ("bomb_3.gif"));
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		//初始化三张图片
		image1 =Toolkit.getDefaultToolkit().getImage(Mypanel.class.getResource("/bomb_1.gif"));
		image2 =Toolkit.getDefaultToolkit().getImage(Mypanel.class.getResource("/bomb_2.gif"));
		image3 =Toolkit.getDefaultToolkit().getImage(Mypanel.class.getResource("/bomb_3.gif"));
	}
	//画出提示信息
	public void showinfo(Graphics g)
	{
		//画出提示信息坦克(不参与战斗)
		this.drawtank(80, 330, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(recorder.getEnnum()+"", 110, 350);
		this.drawtank(130, 330, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(recorder.getMylife()+"", 160, 350);
		
		//画出玩家总成绩
		g.setColor(Color.black);
		Font f=new Font("宋体",Font.BOLD,20);
		g.setFont(f);
		g.drawString("您的总成绩", 420, 30);
		
		this.drawtank(420, 60, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(recorder.getAllennum()+"", 460, 80);
	}
	//重写paint
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		//g.setColor(Color.cyan);
		//画出提示信息
		this.showinfo(g);
		//画出自己的坦克
		if(hero.isLive==true)
		{
			this.drawtank(hero.getX(), hero.getY(), g, this.hero.direct, 1);
		}
		//从ss中取出每颗子弹并画出
		for(int i=0;i<hero.ss.size();i++)
		{
			shot myshot=hero.ss.get(i);
			//画出子弹,画出一个子弹
			if(myshot!=null&&myshot.isLive==true)
			{
				g.draw3DRect(myshot.x, myshot.y, 1, 1, false);
			}
			if(myshot.isLive==false)
			{
				//从ss中删除该子弹
				hero.ss.remove(i);   //?????
			}
		}
		//画出炸弹
		for(int i=0;i<bombs.size();i++)
		{
			//取出炸弹
			bomb b=bombs.get(i);
			if(b.life>6)
			{
				g.drawImage(image1, b.x, b.y, 30, 30, this);
			}else if(b.life>4)
			{
				g.drawImage(image2, b.x, b.y, 30, 30, this);
			}else
			{
				g.drawImage(image3, b.x, b.y, 30, 30, this);
			}
		    //让b的生命值减少	
			b.lifedown();
			//如果炸弹生命值为0，就把该炸弹从bombs向量中去掉
			if(b.life==0)
			{
				bombs.remove(b);
			}	
		}
		//画出敌人的坦克,同时画出敌人子弹
		for(int i=0;i<ets.size();i++)
		{
			enemytank et =ets.get(i);
			if(et.isLive==true)
			{	
				this.drawtank(et.getX(), et.getY(), g, et.getDirect(), 0);
				//再画出敌人的子弹
				for(int j=0;j<et.ss.size();j++)
				{
					//取出子弹
					shot enemShot=et.ss.get(j);
					if(enemShot.isLive==true)
					{
						g.draw3DRect(enemShot.x, enemShot.y, 1, 1, false);
					}else 
					{
						//如果敌人的zidan死亡,就从Vector去掉
						et.ss.remove(enemShot);
					}
				}
			}
		}
	}
	//判断我的子弹是否击中敌人的坦克
	public void hitenemytank()
	{
		//判断是否击中敌人的坦克
		for(int i=0;i<hero.ss.size();i++)
		{
			//取出子弹
			shot myShot =hero.ss.get(i);
			//判断子弹是否死了
			if(myShot.isLive==true)
			{
				//取出每个敌人坦克，与它判断
				for(int j=0;j<ets.size();j++)
				{
				 	//取出坦克
					enemytank et = ets.get(j);
					if(et.isLive==true)
					{
						this.hittank(myShot, et);
						if(et.isLive==false)
						{
							recorder.reduceennum();
							recorder.addallennum();
						}
					}		
				}
			}
			
		}
	}
	//我的子弹是否击中敌人
	public void hitme()
	{
		//取出每一个敌人的坦克
		for(int i=0;i<ets.size();i++)
		{
			//取出坦克
			enemytank et=ets.get(i);
			//取出每一颗子弹
			for(int j=0;j<et.ss.size();j++)
			{
				//取出子弹
				shot enemysShot =et.ss.get(j);
				hittank(enemysShot, hero);
			}
		}
		if(hero.isLive==false)
		{
			recorder.reducelife();
		}
	}
	//写一个函数专门判断子弹是否击中坦克
	public void hittank(shot s,Tank et)
	{
//		//判断该坦克的方向，因为我们的坦克并不是正方形
//		switch(et.direct)
//		{
//		//如果敌人坦克的方向是向上或者向下
//		case 0:
//		case 2:
			if((s.x>et.x&&s.x<et.x+20&&s.y>et.y&&s.y<et.y+30)||(s.x>et.x&&s.x<et.x+30&&s.y>et.y&&s.y<et.y+20))
			{
				//击中
				//子弹死亡
				s.isLive=false;
				//敌人坦克死亡
				et.isLive=false;
				//创建一颗炸弹，放入vector
				bomb b= new bomb(et.x,et.y);
				//放入vector
				bombs.add(b);
			}
//		case 1:
//		case 3:
//			if(s.x>et.x&&s.x<et.x+30&&s.y>et.y&&s.y<et.y+20)
//			{
//				//击中
//				//子弹死亡
//				s.isLive=false;
//				//敌人坦克死亡
//				et.isLive=false;
//				//创建一颗炸弹，放入vector
//				bomb b= new bomb(et.x,et.y);
//				//放入vector
//				bombs.add(b);
//			}
//		}
	}
	//画出坦克的函数
	public void drawtank(int x,int y,Graphics g,int direct,int type)
	{
		//判断什么类型的坦克
		switch(type)
		{
		case 0:
			g.setColor(Color.cyan);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		//判断方向
		switch(direct)
		{
		//向上
		case 0:
			//画出我的坦克(到时再封装成一个函数)
			//1.画出左边的矩形
			g.fill3DRect(x, y, 5, 30,false);
			//2.画出右面的矩形
			g.fill3DRect(x+16, y, 5,30,false);
			//画出中间矩形
			g.fill3DRect(x+5, y+5, 12, 20,false);
			//画出圆形
			g.fillOval(x+5, y+10, 10, 10);
			//画出线
			g.drawLine(x+10, y+15, x+10, y);	
			break;
		 case 1:
			//炮筒向右
			//画出上面的矩形
			 g.fill3DRect(x, y, 30, 5,false);
			 //画出下面的矩形
			 g.fill3DRect(x, y+15, 30, 5,false);
			 //画出中间的矩形
			 g.fill3DRect(x+5, y+5, 20, 10,false);
			 //画出圆形
			 g.fillOval(x+10, y+5, 10, 10);
			 //画出线
			 g.drawLine(x+15, y+10, x+30, y+10);
			 break;
		  case 2:
			  //向下
			  //1.画出左边的矩形
			  g.fill3DRect(x, y, 5, 30,false);
			  //2.画出右面的矩形
			  g.fill3DRect(x+16, y, 5,30,false);
			  //画出中间矩形
			  g.fill3DRect(x+5, y+5, 12, 20,false);
			  //画出圆形
			  g.fillOval(x+5, y+10, 10, 10);
			  //画出线
			  g.drawLine(x+10, y+15, x+10, y+30);	
			  break;
		   case 3:
			  //向左
			  //画出上面的矩形
			  g.fill3DRect(x, y, 30, 5,false);
			  //画出下面的矩形
			  g.fill3DRect(x, y+15, 30, 5,false);
			  //画出中间的矩形
			  g.fill3DRect(x+5, y+5, 20, 10,false);
			  //画出圆形
			  g.fillOval(x+10, y+5, 10, 10);
			  //画出线
			  g.drawLine(x+15, y+10, x, y+10);
			  break;
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	//键按下处理 a 表示向左 s 表示向下 d 表示向右 w 表示向上
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(flag==0)
		{
			if(e.getKeyCode()==KeyEvent.VK_W)
			{
				//设置我的坦克的方向
				this.hero.setDirect(0);
				this.hero.moveUp();
			}else if(e.getKeyCode()==KeyEvent.VK_D)
			{
				//向右
				this.hero.setDirect(1);	
				this.hero.moveRight();
			}else if(e.getKeyCode()==KeyEvent.VK_S)
			{
				//向下
				this.hero.setDirect(2);
				this.hero.moveDown();
			}else if(e.getKeyCode()==KeyEvent.VK_A)
			{
				//向左
				this.hero.setDirect(3);
				this.hero.moveLeft();
			}
			if(e.getKeyCode()==KeyEvent.VK_J)
			{
				if(hero.isLive==true)
				{
					//判断玩家是否按下j
					//开火
					if(this.hero.ss.size()<5)
					{
						this.hero.shotenemy();
					}	
				}
			}
		}
		if(e.getKeyCode()==KeyEvent.VK_SPACE)
		{
			synchronized (this) {
				if(flag==0)
				{
					flag=1;
					herobuff=hero.speed;
					hero.speed=0;
					for(int i=0;i<hero.ss.size();i++)
					{
						shot s=hero.ss.get(i);
						heroshotbuff=s.speed;
						s.speed=0;
					}
					
					for(int i=0;i<this.ets.size();i++)
					{
						enemytank et=ets.get(i);
						enemytankbuff=et.speed;
						et.speed=0;
						et.flagdir=0;
						for(int j=0;j<et.ss.size();j++)
						{
							shot t=et.ss.get(j);
							enemytankshotbuff=t.speed;
							t.speed=0;
						}	
					}
				}else
				{
					flag=0;
					hero.speed=herobuff;
					for(int i=0;i<this.hero.ss.size();i++)
					{
						shot s=this.hero.ss.get(i);
						s.speed=heroshotbuff;
					}
					
					for(int i=0;i<this.ets.size();i++)
					{
						enemytank et=this.ets.get(i);
						et.speed=enemytankbuff;
						et.flagdir=1;
						for(int j=0;j<et.ss.size();j++)
						{
							shot t=et.ss.get(j);
							t.speed=enemytankshotbuff;
						}	
					}
					
				}
			}
		}
		//必须重新绘制panel
		//this.repaint();
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//每隔100毫秒去重绘
		while(true)
		{
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO: handle exception
			}
			if(hero.isLive==true)
			{
				hitenemytank();
				//判断敌人的子弹是否击中我了
				this.hitme();
			}
			//重绘
			this.repaint();
		}
	}	
}

