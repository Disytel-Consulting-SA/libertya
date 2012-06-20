package org.openXpertya.util;

import org.openXpertya.model.MUser;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.UserAuthData;
import org.openXpertya.util.Util;

public class AUKUserAuthModel extends AUserAuthModel {

	@Override
	public MUser searchUser(UserAuthData userAuthData) {
		return MUser.get(Env.getCtx(), userAuthData.getUserName(),
				userAuthData.getPassword(), false);
	}

	@Override
	public CallResult preValidateAuthorization(UserAuthData userAuthData) {
		CallResult result = new CallResult();
		// 1) Validar que la clave no esté vacía
		if (Util.isEmpty(userAuthData.getPassword(), true)
				&& Util.isEmpty(userAuthData.getUserID(), true)) {
			result.setMsg(Msg.getMsg(Env.getCtx(), "NoPass"), true);
		}
		return result;
	}

	@Override
	public CallResult postValidateAuthorization(UserAuthData userAuthData) {
		// TODO Auto-generated method stub
		return null;
	}

}
