package MyServer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import Entity.User;
import GameController.GameController;
import Tools.CommonResponse;
import Tools.DBQuery;
import Tools.DatabaseUtil;
import net.sf.json.JSONObject;

@ServerEndpoint("/websocket/Login")
public class WebSocket_Login {
	@OnOpen
	public void onOpen() {
		System.out.println("Login::WEBopen");
	}

	@OnClose
	public void onClose() {
		System.out.println("Login::WEBclose");
	}

	@OnMessage
	public void onMessage(Session session, String msg) {
		CommonResponse response = new CommonResponse();
		System.out.println(msg);
		if (msg.equals("{}")) {
			response.setResMsg("OK");
		} else {

			JSONObject obj = JSONObject.fromObject(msg);
			String Code = obj.getString("Code");

			DatabaseUtil db = new DatabaseUtil();
			try {
				if (Code.equals("L")) {
					ResultSet DBres = db.query(DBQuery.Login(obj.getString("uName"), obj.getString("pWord")));
					if (DBres.next()) {
						boolean login = GameController.addPlayer(
								new User(DBres.getString("userName"), DBres.getInt("score"), DBres.getInt("avatar")));
						if (login) {
							response.setResult("001", "LoginSuccess");
						} else {
							User mu = GameController.findPlayer(DBres.getString("userName"));
							if (mu != null) {
								response.setResult("005", mu.getState().toString());
							}
						}
					} else {
						response.setResult("003", "LoginError");
					}
				} else {
					ResultSet DBres = db.query(DBQuery.FindUser(obj.getString("uName")));
					if (DBres.next()) {
						response.setResult("004", "UserNameExists");
					} else {
						int resCode = db.update(DBQuery.Reg(obj.getString("uName"), obj.getString("pWord")));
						if (resCode == 1) {
							response.setResult("001", "RegSuccess");
						} else {
							response.setResult("002", "UnknownError");
						}
					}
				}
				db.closeConnection();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				response.setResult("000", "DatabaseError");
			}
		}
		if (session.isOpen()) {
			try {
				// 将websocket传过来的值返回回去
				String resStr = JSONObject.fromObject(response).toString();
				session.getBasicRemote().sendText(resStr);
				// System.out.println(resStr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
