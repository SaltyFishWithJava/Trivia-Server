package MyServer;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import Entity.User;
import GameController.GameController;
import Tools.CommonResponse;
import net.sf.json.JSONObject;

@ServerEndpoint("/websocket/PlayerList")
public class WebSocket_PlayerList {
	@OnOpen
	public void onOpen() {
		System.out.println("PL::WEBopen");
	}

	@OnClose
	public void onClose() {
		System.out.println("PL::WEBCLOSE");
	}

	@OnMessage
	public void onMessage(Session session, String msg) {
		CommonResponse response = new CommonResponse();
		System.out.println(msg);
		if (msg.equals("{}")) {
			response.setResMsg("PLOK");
		} else {
			JSONObject jsMsg = JSONObject.fromObject(msg);
			if (jsMsg.getString("Code").equals("R")) {
				System.out.println("R");
				GameController.getPlayerList(jsMsg.getString("uN"), response);
			} else if (jsMsg.getString("Code").equals("RP")) {
				System.out.println("RP");
				User user = GameController.findPlayer(jsMsg.getString("uN"));
				user.putInforAlone(response);
			} else {
				response.setResMsg("RequestError");
			}
		}
		if (session.isOpen()) {
			try {
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
