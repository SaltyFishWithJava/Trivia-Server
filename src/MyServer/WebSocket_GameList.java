package MyServer;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import GameController.GameController;
import Tools.CommonResponse;
import net.sf.json.JSONObject;

@ServerEndpoint("/websocket/GameList")
public class WebSocket_GameList {
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
		System.out.println(msg);
		if (msg.equals("{}")) {
			response.setResMsg("GLOK");
		} else {
			JSONObject jsMsg = JSONObject.fromObject(msg);
			if (jsMsg.getString("Code").equals("R")) {
				System.out.println("R");
				GameController.getGameList(response);
			} else if (jsMsg.getString("Code").equals("C")) {
				System.out.println("C");
				if (GameController.addGame(jsMsg)) {
					response.setResMsg("CreateRoomSuccess");
				} else {
					response.setResMsg("CreateRoomError");
				}
			} else {
				response.setResMsg("RequestError");
			}
		}
		if (session.isOpen()) {
			try {
				// 将websocket传过来的值返回回去
				String resStr = JSONObject.fromObject(response).toString();
				session.getBasicRemote().sendText(resStr);
				System.out.println(resStr);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
