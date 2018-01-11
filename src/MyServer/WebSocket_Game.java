package MyServer;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import Entity.Game;
import Entity.User;
import Entity.User.State;
import GameController.GameController;
import GameController.GameLogic;
import Tools.CommonResponse;
import net.sf.json.JSONObject;

@ServerEndpoint("/websocket/Game")
public class WebSocket_Game {
	GameLogic gl = null;
	Game test = null;

	@OnOpen
	public void onOpen() {
		System.out.println("GL::WEBopen");
	}

	@OnClose
	public void onClose() {
		System.out.println("GL::WEBCLOSE");
	}

	@OnMessage
	public void onMessage(Session session, String msg) {
		CommonResponse response = new CommonResponse();
		JSONObject jsMsg = JSONObject.fromObject(msg);
		System.out.println(msg);
		if (jsMsg.getString("Code").equals("Check")) {
			response.setResMsg("GaOK");
			GameController.findPlayer(jsMsg.getString("uName")).setLastReadTime();
		} else {
			test = GameController.findGame(jsMsg.getString("gN"));
			if (test.getHost() == null) {
				test.setHost(new User(jsMsg.getString("uN"), 0, 0));
			}
			if (jsMsg.getString("Code").equals("Init")) {
				System.out.println("Init");
				test.addPlayer(new User(jsMsg.getString("uN"), 0, 0));
				test.addSession(jsMsg.getString("uN"), session);
				test.putInformation_(response);
				response.setResMsg("Init");
				for (Session ms : test.getSessionMap().values()) {
					String resStr = JSONObject.fromObject(response).toString();
					try {
						ms.getBasicRemote().sendText(resStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(resStr);
				}
			} else if (jsMsg.getString("Code").equals("Start")) {
				gl = test.startGame(jsMsg.getString("uN"));
				if (gl != null) {
					gl.putGameInfor(response, true);
					response.setResMsg("StartGame");
				} else {
					response.setResMsg("Fail");
				}
				for (Session ms : test.getSessionMap().values()) {
					String resStr = JSONObject.fromObject(response).toString();
					try {
						ms.getBasicRemote().sendText(resStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(resStr);
				}
			} else if (jsMsg.getString("Code").equals("Ready")) {
				User user = test.findPlayer(jsMsg.getString("uN"));
				if (user != null) {
					user.setState(State.READY);
					test.putInformation_(response);
					response.setResMsg("Init");
				} else {
					response.setResMsg("ReadyFail");
				}
				for (Session ms : test.getSessionMap().values()) {
					String resStr = JSONObject.fromObject(response).toString();
					try {
						ms.getBasicRemote().sendText(resStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(resStr);
				}
			} else if (jsMsg.getString("Code").equals("Roll")) {
				if (gl == null) {
					gl = GameController.findPlayingGame(jsMsg.getString("gN"));
				}
				gl.rollAndUpdate(response);
				System.out.println("Roll");
				for (Session ms : test.getSessionMap().values()) {
					String resStr = JSONObject.fromObject(response).toString();
					try {
						ms.getBasicRemote().sendText(resStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(resStr);
				}
			} else if (jsMsg.getString("Code").equals("Remove")) {
				User user = test.findPlayer(jsMsg.getString("uN"));
				test.removePlayer(user);
			} else if (jsMsg.getString("Code").equals("Question")) {
				if (gl == null) {
					gl = GameController.findPlayingGame(jsMsg.getString("gN"));
				}
				gl.Check(response, jsMsg.getString("answer"));
				response.getProperty().put("answer", jsMsg.getString("answer"));
				System.out.println("Question");
				for (Session ms : test.getSessionMap().values()) {
					String resStr = JSONObject.fromObject(response).toString();
					try {
						ms.getBasicRemote().sendText(resStr);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(resStr);
				}
				if (response.getResMsg().equals("End")) {

					gl.getGame().stopGame();
				}
			}
		}
	}
	/*
	 * f (session.isOpen()) { try { // 将websocket传过来的值返回回去 String resStr =
	 * JSONObject.fromObject(response).toString();
	 * session.getBasicRemote().sendText(resStr); System.out.println(resStr); }
	 * catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
}
