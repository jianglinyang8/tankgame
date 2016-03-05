package tankgame;
//1.����̹��
//2.�ҵ�̹�˿��������ƶ�
//3.���Է����ӵ����ӵ��������5��
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
	
	//����һ����ʼ�����
	mystartpanel msp=null;
	
	//��������Ҫ�Ĳ˵�
	JMenuBar jmb =null;
	//��ʼ��Ϸ
	JMenu jm1=null;
	JMenuItem jmi1=null;
	//�˳�ϵͳ
	JMenuItem jmi2=null;
	//�����˳�
	JMenuItem jmi3=null;
	//ȥ��һ��
	JMenuItem jmi4=null;
	
	//�ж���ȥ�Ͼֻ�������Ϸ
	public static String flag1="newgame";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		tankgame tk= new tankgame();
	}
	//���캯��
	public tankgame()
	{
//		mp=new Mypanel();
//		//����mp�߳�
//		Thread t= new Thread(mp);
//		t.start();
//		this.add(mp);
//		//ע�����
//		this.addKeyListener(mp);
		
		//�����˵����˵�ѡ��
		jmb=new JMenuBar();
		jm1=new JMenu("��Ϸ(G)");
		//�������Ƿ�
		jm1.setMnemonic('G');
		jmb.add(jm1);
		jmi1=new JMenuItem("��ʼ��Ϸ(N)");
		jmi1.setMnemonic('N');
		jm1.add(jmi1);
		
		jmi2=new JMenuItem("�˳���Ϸ(E)");
		jmi2.setMnemonic('E');
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		jm1.add(jmi2);
		
		jmi3=new JMenuItem("�����˳�(c)");
		jmi3.setMnemonic('S');
		jmi3.addActionListener(this);
		jmi3.setActionCommand("saveexit");
		jm1.add(jmi3);
		
		jmi4=new JMenuItem("�����Ͼ���Ϸ(C)");
		jmi4.setMnemonic('C');
		jmi4.addActionListener(this);
		jmi4.setActionCommand("continue");
		jm1.add(jmi4);
		
		
		this.setJMenuBar(jmb);
		
		//��jmi1��Ӧ
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
		//���û���ͬ�ĵ��������ͬ�Ĵ���
		if(e.getActionCommand().equals("newgame"))
		{
			//����ս�����
			mp=new Mypanel();
			//����mp�߳�
			Thread t= new Thread(mp);
			t.start();
			//��ɾ���ɵĿ�ʼ���
			this.remove(msp);
			this.add(mp);
			//ע�����
			this.addKeyListener(mp);
			//��ʾ��ˢ��jframe
			this.setVisible(true);
		}else if(e.getActionCommand().equals("exit"))
		{
			//�û�������˳�ϵͳ�˵�
			//������ٵ�������
			recorder.keeprecorder();
			System.exit(0);
		}else if(e.getActionCommand().equals("saveexit"))
		{ 
			//����
			//������ٵ��˵������͵��˵�����
			recorder.setEts(mp.ets);
			recorder.keeprecandenemytank();
			//�˳�
			System.exit(0);
		}else if(e.getActionCommand().equals("continue"))
		{
			recorder.getnodesandennums();
			flag1="continue";
			//����ս�����
			mp=new Mypanel();	
			//����mp�߳�
			Thread t= new Thread(mp);
			t.start();
			//��ɾ���ɵĿ�ʼ���
			this.remove(msp);
			this.add(mp);
			//ע�����
			this.addKeyListener(mp);
			//��ʾ��ˢ��jframe
			this.setVisible(true);
		}
	}
}

//����һ����ʾ����
class mystartpanel extends JPanel implements Runnable
{
	//��˸Ч��
	int times=0;
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		times++;
		//��ʾ��Ϣ
		if(times%2==0)
		{
			times=0;
			g.setColor(Color.yellow);
			//������Ϣ������                                               ����
			Font myfont=new Font("������κ", Font.BOLD, 30);
			g.setFont(myfont);
			g.drawString("stage: 1", 150, 150);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			//����
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO: handle exception
			}
			//�ػ�
			this.repaint();
		}
	}
}

//�ҵ����
class Mypanel extends JPanel implements KeyListener,Runnable
{
	//��ͣ��־λ
	int flag=0;
	int herobuff;
	int heroshotbuff;
	int enemytankbuff;
	int enemytankshotbuff;

	//����һ���ҵ�̹��
	Hero hero=null;
	//������˵�̹����
	Vector <enemytank> ets= new Vector<enemytank>();
	
	//����ը������
	Vector<bomb> bombs=new Vector<bomb>();
	//����̹������
	int ensize=3;
	
	//��������ͼƬ,����ͼƬ�������һ��ը��
	Image image1=null;
	Image image2=null;
	Image image3=null;
	
	//���캯��
	public Mypanel()
	{
		//�ָ���¼����
		recorder.getrecorder();
		
		hero = new Hero(200,150);
		hero.speed=3;
		if(tankgame.flag1.equals("newgame"))
		{
			//��ʼ�����˵�̹��
			for(int i=0;i<ensize;i++)
			{
				//����һ�����˵�̹�˶���
				enemytank et =new enemytank((i+1)*50, 0);
				et.setColor(0);
				et.setDirect(2);
				//��mypanel�ĵ���̹�����������õ���̹��
				et.setets(ets);
				//��������̹��
				Thread t=new Thread(et);
				t.start();
				//������̹�����һ���ӵ�
				shot s = new shot(et.x+10, et.y+30, 2);
				//�ӵ��������̹��
				et.ss.add(s);
				Thread t2 =new Thread(s);
				t2.start();
				//����
				ets.add(et);
			}
		}
		else
		{
			//��ʼ�����˵�̹��
			for(int i=0;i<recorder.nodes.size();i++)
			{
				node nd=recorder.nodes.get(i);
				//����һ�����˵�̹�˶���
				enemytank et =new enemytank(nd.x, nd.y);
				et.setColor(0);
				et.setDirect(nd.direct);
				//��mypanel�ĵ���̹�����������õ���̹��
				et.setets(ets);
				//��������̹��
				Thread t=new Thread(et);
				t.start();
				//������̹�����һ���ӵ�
				shot s = new shot(et.x+10, et.y+30, 2);
				//�ӵ��������̹��
				et.ss.add(s);
				Thread t2 =new Thread(s);
				t2.start();
				//����
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
		//��ʼ������ͼƬ
		image1 =Toolkit.getDefaultToolkit().getImage(Mypanel.class.getResource("/bomb_1.gif"));
		image2 =Toolkit.getDefaultToolkit().getImage(Mypanel.class.getResource("/bomb_2.gif"));
		image3 =Toolkit.getDefaultToolkit().getImage(Mypanel.class.getResource("/bomb_3.gif"));
	}
	//������ʾ��Ϣ
	public void showinfo(Graphics g)
	{
		//������ʾ��Ϣ̹��(������ս��)
		this.drawtank(80, 330, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(recorder.getEnnum()+"", 110, 350);
		this.drawtank(130, 330, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(recorder.getMylife()+"", 160, 350);
		
		//��������ܳɼ�
		g.setColor(Color.black);
		Font f=new Font("����",Font.BOLD,20);
		g.setFont(f);
		g.drawString("�����ܳɼ�", 420, 30);
		
		this.drawtank(420, 60, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(recorder.getAllennum()+"", 460, 80);
	}
	//��дpaint
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 400, 300);
		//g.setColor(Color.cyan);
		//������ʾ��Ϣ
		this.showinfo(g);
		//�����Լ���̹��
		if(hero.isLive==true)
		{
			this.drawtank(hero.getX(), hero.getY(), g, this.hero.direct, 1);
		}
		//��ss��ȡ��ÿ���ӵ�������
		for(int i=0;i<hero.ss.size();i++)
		{
			shot myshot=hero.ss.get(i);
			//�����ӵ�,����һ���ӵ�
			if(myshot!=null&&myshot.isLive==true)
			{
				g.draw3DRect(myshot.x, myshot.y, 1, 1, false);
			}
			if(myshot.isLive==false)
			{
				//��ss��ɾ�����ӵ�
				hero.ss.remove(i);   //?????
			}
		}
		//����ը��
		for(int i=0;i<bombs.size();i++)
		{
			//ȡ��ը��
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
		    //��b������ֵ����	
			b.lifedown();
			//���ը������ֵΪ0���ͰѸ�ը����bombs������ȥ��
			if(b.life==0)
			{
				bombs.remove(b);
			}	
		}
		//�������˵�̹��,ͬʱ���������ӵ�
		for(int i=0;i<ets.size();i++)
		{
			enemytank et =ets.get(i);
			if(et.isLive==true)
			{	
				this.drawtank(et.getX(), et.getY(), g, et.getDirect(), 0);
				//�ٻ������˵��ӵ�
				for(int j=0;j<et.ss.size();j++)
				{
					//ȡ���ӵ�
					shot enemShot=et.ss.get(j);
					if(enemShot.isLive==true)
					{
						g.draw3DRect(enemShot.x, enemShot.y, 1, 1, false);
					}else 
					{
						//������˵�zidan����,�ʹ�Vectorȥ��
						et.ss.remove(enemShot);
					}
				}
			}
		}
	}
	//�ж��ҵ��ӵ��Ƿ���е��˵�̹��
	public void hitenemytank()
	{
		//�ж��Ƿ���е��˵�̹��
		for(int i=0;i<hero.ss.size();i++)
		{
			//ȡ���ӵ�
			shot myShot =hero.ss.get(i);
			//�ж��ӵ��Ƿ�����
			if(myShot.isLive==true)
			{
				//ȡ��ÿ������̹�ˣ������ж�
				for(int j=0;j<ets.size();j++)
				{
				 	//ȡ��̹��
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
	//�ҵ��ӵ��Ƿ���е���
	public void hitme()
	{
		//ȡ��ÿһ�����˵�̹��
		for(int i=0;i<ets.size();i++)
		{
			//ȡ��̹��
			enemytank et=ets.get(i);
			//ȡ��ÿһ���ӵ�
			for(int j=0;j<et.ss.size();j++)
			{
				//ȡ���ӵ�
				shot enemysShot =et.ss.get(j);
				hittank(enemysShot, hero);
			}
		}
		if(hero.isLive==false)
		{
			recorder.reducelife();
		}
	}
	//дһ������ר���ж��ӵ��Ƿ����̹��
	public void hittank(shot s,Tank et)
	{
//		//�жϸ�̹�˵ķ�����Ϊ���ǵ�̹�˲�����������
//		switch(et.direct)
//		{
//		//�������̹�˵ķ��������ϻ�������
//		case 0:
//		case 2:
			if((s.x>et.x&&s.x<et.x+20&&s.y>et.y&&s.y<et.y+30)||(s.x>et.x&&s.x<et.x+30&&s.y>et.y&&s.y<et.y+20))
			{
				//����
				//�ӵ�����
				s.isLive=false;
				//����̹������
				et.isLive=false;
				//����һ��ը��������vector
				bomb b= new bomb(et.x,et.y);
				//����vector
				bombs.add(b);
			}
//		case 1:
//		case 3:
//			if(s.x>et.x&&s.x<et.x+30&&s.y>et.y&&s.y<et.y+20)
//			{
//				//����
//				//�ӵ�����
//				s.isLive=false;
//				//����̹������
//				et.isLive=false;
//				//����һ��ը��������vector
//				bomb b= new bomb(et.x,et.y);
//				//����vector
//				bombs.add(b);
//			}
//		}
	}
	//����̹�˵ĺ���
	public void drawtank(int x,int y,Graphics g,int direct,int type)
	{
		//�ж�ʲô���͵�̹��
		switch(type)
		{
		case 0:
			g.setColor(Color.cyan);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		//�жϷ���
		switch(direct)
		{
		//����
		case 0:
			//�����ҵ�̹��(��ʱ�ٷ�װ��һ������)
			//1.������ߵľ���
			g.fill3DRect(x, y, 5, 30,false);
			//2.��������ľ���
			g.fill3DRect(x+16, y, 5,30,false);
			//�����м����
			g.fill3DRect(x+5, y+5, 12, 20,false);
			//����Բ��
			g.fillOval(x+5, y+10, 10, 10);
			//������
			g.drawLine(x+10, y+15, x+10, y);	
			break;
		 case 1:
			//��Ͳ����
			//��������ľ���
			 g.fill3DRect(x, y, 30, 5,false);
			 //��������ľ���
			 g.fill3DRect(x, y+15, 30, 5,false);
			 //�����м�ľ���
			 g.fill3DRect(x+5, y+5, 20, 10,false);
			 //����Բ��
			 g.fillOval(x+10, y+5, 10, 10);
			 //������
			 g.drawLine(x+15, y+10, x+30, y+10);
			 break;
		  case 2:
			  //����
			  //1.������ߵľ���
			  g.fill3DRect(x, y, 5, 30,false);
			  //2.��������ľ���
			  g.fill3DRect(x+16, y, 5,30,false);
			  //�����м����
			  g.fill3DRect(x+5, y+5, 12, 20,false);
			  //����Բ��
			  g.fillOval(x+5, y+10, 10, 10);
			  //������
			  g.drawLine(x+10, y+15, x+10, y+30);	
			  break;
		   case 3:
			  //����
			  //��������ľ���
			  g.fill3DRect(x, y, 30, 5,false);
			  //��������ľ���
			  g.fill3DRect(x, y+15, 30, 5,false);
			  //�����м�ľ���
			  g.fill3DRect(x+5, y+5, 20, 10,false);
			  //����Բ��
			  g.fillOval(x+10, y+5, 10, 10);
			  //������
			  g.drawLine(x+15, y+10, x, y+10);
			  break;
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	//�����´��� a ��ʾ���� s ��ʾ���� d ��ʾ���� w ��ʾ����
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(flag==0)
		{
			if(e.getKeyCode()==KeyEvent.VK_W)
			{
				//�����ҵ�̹�˵ķ���
				this.hero.setDirect(0);
				this.hero.moveUp();
			}else if(e.getKeyCode()==KeyEvent.VK_D)
			{
				//����
				this.hero.setDirect(1);	
				this.hero.moveRight();
			}else if(e.getKeyCode()==KeyEvent.VK_S)
			{
				//����
				this.hero.setDirect(2);
				this.hero.moveDown();
			}else if(e.getKeyCode()==KeyEvent.VK_A)
			{
				//����
				this.hero.setDirect(3);
				this.hero.moveLeft();
			}
			if(e.getKeyCode()==KeyEvent.VK_J)
			{
				if(hero.isLive==true)
				{
					//�ж�����Ƿ���j
					//����
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
		//�������»���panel
		//this.repaint();
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//ÿ��100����ȥ�ػ�
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
				//�жϵ��˵��ӵ��Ƿ��������
				this.hitme();
			}
			//�ػ�
			this.repaint();
		}
	}	
}

