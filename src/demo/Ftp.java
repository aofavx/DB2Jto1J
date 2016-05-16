package demo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
 
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
/**
 * 对ftp上传下载的操作
 *
 */
public class Ftp {
	Logger logger=Logger.getLogger(this.getClass().getName());
	//ftp客户端
	private FtpClient ftpClient;
	/**
	 * 服务器连接
	 * @param ip 服务器IP
     * @param port 服务器端口
     * @param user 用户名
     * @param password 密码
     * @param path 服务器路径
	 */
	public void connectServer(String ip,int port,String user,String password,String path){
        try {
            ftpClient = new FtpClient();
            ftpClient.openServer(ip, port);
            ftpClient.login(user, password);
            // 设置成2进制传输
            ftpClient.binary();
            if (path.length() != 0){
                //把远程系统上的目录切换到参数path所指定的目录
                ftpClient.cd(path);
            }
            ftpClient.binary();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
	
	}
	/**
     * 关闭连接
     */
    public void closeConnect() {
        try {
            ftpClient.closeServer();
            logger.info("disconnect success");
        } catch (IOException ex) {
        	logger.error(ex);
//            throw new RuntimeException(ex);
        }
    }
	/**
     * 下载文件
     * @param remoteFile 远程文件路径(服务器端)
     * @param localFile 本地文件路径(客户端)
     */
    public void download(String remoteFile, String localFile) {
        TelnetInputStream is = null;
        FileOutputStream os = null;
        try {
            //获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。
            is = ftpClient.get(remoteFile);
            File file_in = new File(localFile);
            if(!file_in.getParentFile().exists()){
            	file_in.getParentFile().mkdirs();
            }
            os = new FileOutputStream(file_in);
            byte[] bytes = new byte[1024];
            int c = -1;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
        } catch (IOException ex) {
           logger.error(ex);
//           throw new RuntimeException(ex);
        }finally{
            try {
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(os != null){
                        os.close();
                    }
                } catch (IOException e) {
                	 logger.error(e);
                }
            }
        }
    }
    
    public InputStream downloadToStream(String remoteFile) throws IOException {
    	TelnetInputStream is = ftpClient.get(remoteFile);
		return is;
    }
    /**
     * 文件上传
     * @param localFile 本地文件（客户端）
     * @param remoteFile远程文件（服务端）
     */
    public void upLoad(String localFile,String remoteFile){
    	TelnetOutputStream os=null;
    	FileInputStream is=null;
    	try {
			os=ftpClient.put(remoteFile);
			is=new FileInputStream(new File(localFile));
			byte[] bytes=new byte[1024];
			int c=-1;
			while((c=is.read(bytes))!=-1){
				os.write(bytes,0,c);
			}
			logger.info("upLoad success");
		} catch (IOException e) {
			logger.error(e);
//			throw new RuntimeException(e);
		}finally{
			try {
				if(is!=null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(os!=null){
						os.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	
    }
    public static void main(String[]args){
//    	Ftp ftp=new Ftp();
//    	ftp.connectServer("127.0.0.1",21,"wwj","wwj","");
//    	ftp.download("a/up.txt","D:/up.txt");
//    	ftp.closeConnect();

    	
    }
}
