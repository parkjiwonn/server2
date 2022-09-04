import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;

public class ChatSverThread extends Thread {

    // 소켓통신하기 위한 변수들
    private Socket m_socket;
    private String m_ID;

    // db 접속하기 위한 변수들
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://3.39.168.165:3306/my-app";
    static final String USERNAME = "jiwon";
    static final String PASSWORD = "1234";

    @Override
    public void run() {
        super.run();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            // client에서 보낸 메세지 읽기 위한 stream
            BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
            String text;

            int insertCount = 0; // insert 후 반환값
            conn = null;
            ps = null;
            // Class.forName 메서드 이용해서 JDBC 드라이버 로딩하기
            Class.forName(JDBC_DRIVER); // JDBC 드라이버 연결
            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD); // DB 연결
            System.out.println("\n- MySQL Connection");

            while (true) {

                // 보낸 메세지 한줄씩 읽기
                text = in.readLine();
                // "///" 기준으로 문자열 자르기
                String[] array = text.split("///");

                String user_email = array[0]; // 채팅 보낸 유저 이메일
                String content = array[1]; // 채팅 내용
                String time = array[2]; // 채팅 보낸 시간
                int room_num = Integer.parseInt(array[3]); // 문자열 -> 정수 변환 , 채팅방 숫자 표헌으로 구분
                String nick = array[4]; // 채팅 보낸 사람 닉네임
                String profile = array[5]; // 채팅 보낸 사람 프로필 사진
                String chat_date = array[6]; // 채팅보낸 년월일
                String pre_date = array[7]; // 바로전에 보낸 채팅 시간, 년월일
                // 클라이언트에서 받은 데이터를 db에 저장해야 한다.

                // 클라에서 보낸 데이터 서버에서 잘 split 되는지 확인 -> split 잘 됨.
                System.out.println(user_email);
                System.out.println(content);
                System.out.println(time);
                System.out.println(room_num);
                System.out.println(nick);
                System.out.println(profile);
                System.out.println(chat_date);
                System.out.println(pre_date);



                if(chat_date.equals(pre_date))
                {
                    String sql;
                    sql = "insert into ChatData(roomNum, user, content, time, profile, nick, chat_date) values (?,?,?,?,?,?,?)";
                    ps = conn.prepareStatement(sql);

                    ps.setInt(1, room_num); // 채팅방 구분
                    ps.setString(2, user_email); // 채팅 보낸 유저
                    ps.setString(3, content); // 채팅 내용
                    ps.setString(4, time); // 채팅 보낸 시간
                    ps.setString(5, profile); // 채팅 보낸 유저 프로필 사진
                    ps.setString(6, nick); // 채팅 보낸 유저 닉네임
                    ps.setString(7, chat_date);// 채팅 보낸 년 월 일

                    insertCount = ps.executeUpdate(); // 명렁어 실행 -> 반환값이 int 여서 그럼

                }else
                // 채팅을 보낸 시간이랑 제일 마지막에 보낸 채팅의 시간이 다르면 admin 으로 시간 db에 저장하고 채팅데이터도 저장하기.
                {
                    String time_sql; // 시간 저장하는 sql
                    time_sql = "insert into ChatData(roomNum, user, content, time, profile, nick, chat_date) values(?,?,?,?,?,?,?)";
                    ps = conn.prepareStatement(time_sql);

                    ps.setInt(1, room_num); // 채팅방 구분
                    ps.setString(2, "admin"); // 채팅 보낸 유저
                    ps.setString(3, chat_date); // 채팅 내용
                    ps.setString(4, chat_date); // 채팅 보낸 시간
                    ps.setString(5, chat_date); // 채팅 보낸 유저 프로필 사진
                    ps.setString(6, chat_date); // 채팅 보낸 유저 닉네임
                    ps.setString(7, chat_date);// 채팅 보낸 년 월 일
                    insertCount = ps.executeUpdate(); // 명렁어 실행 -> 반환값이 int 여서 그럼

                    String sql;
                    sql = "insert into ChatData(roomNum, user, content, time, profile, nick, chat_date) values (?,?,?,?,?,?,?)";
                    ps = conn.prepareStatement(sql);

                    ps.setInt(1, room_num); // 채팅방 구분
                    ps.setString(2, user_email); // 채팅 보낸 유저
                    ps.setString(3, content); // 채팅 내용
                    ps.setString(4, time); // 채팅 보낸 시간
                    ps.setString(5, profile); // 채팅 보낸 유저 프로필 사진
                    ps.setString(6, nick); // 채팅 보낸 유저 닉네임
                    ps.setString(7, chat_date);// 채팅 보낸 년 월 일

                    insertCount = ps.executeUpdate(); // 명렁어 실행 -> 반환값이 int 여서 그럼
                }

                if (text.contains("///")) {
                    System.out.println(text);
                    // 클라이언트에서 보낸 문자열을 나눠서 db에 저장해야 한다.

                } else {
                    System.out.println(text + "님이 접속하셨습니다.");
                }

                if (text != null) {
                    // 현재 채팅방에 접속해 있는 client들 에게 전달받은 메세지를 다시 보내주기,
                    for (int i = 0; i < MainServer.m_OutputList.size(); ++i) {
                        // 출력해라.
                        MainServer.m_OutputList.get(i).println(text);
                        MainServer.m_OutputList.get(i).flush();
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(ps!=null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }

                if(conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }
    public void setSocket(Socket _socket){

        m_socket = _socket;
    }

}
