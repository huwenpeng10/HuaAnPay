package com.example.mrxu.allthread;

import java.util.Map;
import java.util.TreeMap;

import fncat.qpos.Controller.StatusCode;

public class ErrorHint {public static Map<Integer, String> errorMap = new TreeMap<Integer, String>();

	// "~错误码：" + Msg;
	public static void InitErrorMsg() {
		if (errorMap.size() == 0) {
			errorMap.put(StatusCode.SUCCESS, "成功！");
			errorMap.put(StatusCode.STATE_BUSY, "正在通讯中，请稍等！");
			errorMap.put(StatusCode.STATE_UNKNOWN, "未知错误！");
			errorMap.put(StatusCode.INITIALIZE_FAIL, "初始化失败！");
			errorMap.put(StatusCode.NOT_INITIALIZE, "未初始化！");
			errorMap.put(StatusCode.STATE_DISCONNECT, "终端断开连接");
			errorMap.put(StatusCode.BT_ADDRESS_ERROR, "蓝牙地址错误");
			errorMap.put(StatusCode.BT_DEVICE_NULL, "本机蓝牙异常");
			errorMap.put(StatusCode.BT_NOT_DISCOVERY_DEVICE, "没有扫描到设备");
			errorMap.put(StatusCode.CONNECTING_INTERRUPTED, "连接中断");
			errorMap.put(StatusCode.CONNECTING_TIMEOUT, "连接超时");

			errorMap.put(StatusCode.PARAM_FORMAT_ERROR, "参数格式错误");
			errorMap.put(StatusCode.SEND_FAIL, "发送失败");
			errorMap.put(StatusCode.SEND_MAC_ERROR, "发送数据MAC加密失败");
			errorMap.put(StatusCode.RECEIVE_DISCONNECT, "获取数据断开连接");
			errorMap.put(StatusCode.RECEIVE_INTERRUPTED, "获取数据中断");
			errorMap.put(StatusCode.COMMUNICATION_TIMEOUT, "通讯数据超时");
			errorMap.put(StatusCode.RECEIVE_DATA_LENGTH_ERROR, "获取的数据长度格式不对");
			errorMap.put(StatusCode.RECEIVE_MAC_FAILURE, "获取的数据MAC校验失败");
			errorMap.put(StatusCode.RECEIVE_CRC_FAILURE, "获取的数据CRC校验失败");
			errorMap.put(StatusCode.RECEIVE_XOR_FAILURE, "获取的数据异或校验失败");
			errorMap.put(StatusCode.RECEIVE_NO_DATA, "没获取到数据");

			errorMap.put(StatusCode.COMMAND_EXECUTE_TIMEOUT, "命令执行超时");
			errorMap.put(StatusCode.AUTH_FAILURE, "安全模块认证失败");
			errorMap.put(StatusCode.ELECTRICITY_FAILURE, "安全模块上电失败或者不存在");
			errorMap.put(StatusCode.OPERATION_FAILURE, "安全模块操作失败");
			errorMap.put(StatusCode.USER_EXIT, "用户退出");
			errorMap.put(StatusCode.MAC_FAILURE, "MAC校验失败");
			errorMap.put(StatusCode.ENCRYPTION_FAILURE, "终端加密失败");
			errorMap.put(StatusCode.LETTER_ATTESTATION, "数字信封验签错误");
			errorMap.put(StatusCode.SELF_DESTRUCTION, "自毁");
			errorMap.put(StatusCode.EXCEPTION, "安全模块状态异常");
			errorMap.put(StatusCode.MANAGE_DEFICIENCY_ERROR, "自检管理服务器公钥没写入错误");
			errorMap.put(StatusCode.CONFIG_PUBLIC_KEY_DEFICIENCY_ERROR, "自检配置服务器公钥没写入错误");
			errorMap.put(StatusCode.WORK_DEFICIENCY_ERROR, "自检工程模式没有禁用错误");
			errorMap.put(StatusCode.KEY_DEFICIENCY_ERROR, "自检公私钥没有生成错误");
			errorMap.put(StatusCode.NO_ACTIVATION_DEFICIENCY_ERROR, "自检侵功能没有激活错误");
			errorMap.put(StatusCode.SLEEP_10_DEFICIENCY_ERROR, "自检休眠时间不为10分钟错误");
			errorMap.put(StatusCode.TERMINAL_DEFICIENCY_ERROR, "自检终端ID没写入错误");
			errorMap.put(StatusCode.PSAMID_DEFICIENCY_ERROR, "自检psamid没写入错误");
			errorMap.put(StatusCode.BANKID_DEFICIENCY_ERROR, "自检bankid没写入错误");
			errorMap.put(StatusCode.MISMATCH_COMMAND, "不匹配的主命令码");
			errorMap.put(StatusCode.MISMATCH_SUB_COMMAND, "不匹配的子命令码");
			errorMap.put(StatusCode.BATTERY_VOLTAGE_ERROR, "获取电池电量失败");
			errorMap.put(StatusCode.SWIPE_JS2_CARD, "刷的是js2卡");
			errorMap.put(StatusCode.PLUG_IC_CARD, "插入IC卡");
			errorMap.put(StatusCode.ANALYSIS_LETTER_ERROR, "解析数字信封长度错误");
			errorMap.put(StatusCode.RETRANSMISSION_DATA_INVALID, "重传数据无效");
			errorMap.put(StatusCode.NOT_DISTINGUISH_HEADER, "不识别的包头");
			errorMap.put(StatusCode.NOT_DISTINGUISH_COMMAND, "不识别的主命令码");
			errorMap.put(StatusCode.NOT_DISTINGUISH_SUB_COMMAND, "不识别的子命令码");
			errorMap.put(StatusCode.VERSION_NOT_COMMAND, "该版本不支持此指令");
			errorMap.put(StatusCode.RANDOM_LENGTH_ERROR, "随机数长度错误");
			errorMap.put(StatusCode.NOT_SUPPORT_COMPONENTS, "不支持的部件");
			errorMap.put(StatusCode.NOT_SUPPORT_MODE, "不支持的模式");
			errorMap.put(StatusCode.DATA_LENGTH_ERROR, "数据域长度错误");
			errorMap.put(StatusCode.TERMINAL_ID_ERROR, "终端ID错误");
			errorMap.put(StatusCode.DATA_CONTENT_WRONG, "数据域内容有误");
			errorMap.put(StatusCode.CHECKSUM_ERROR, "校验和错误");

			errorMap.put(StatusCode.VOID_PACKAGE, "空包，一般单片机用来告知手机发送的请求成功接受，但是没有需要的数据");
			errorMap.put(StatusCode.DATA_INCOMPLETE, "数据接受结束后，接受的数据不全而导致错误");
			errorMap.put(StatusCode.DATA_LOST, "在解析音频数据的时候，发现有数据无法解析而导致数据丢失");
			errorMap.put(StatusCode.CAPTRUE_HEAD_FAILED, "一直捕获不到前导 尝试超过次数");
			errorMap.put(StatusCode.READ_WAV_TIMEOUT, "录取音频的时候一直没有有效音频数据出现，超时");
			errorMap.put(StatusCode.AUDIO_RECORD_NOT_INIT, "record没有被初始化，联想手机上出现过权限被拦截的情况");
			errorMap.put(StatusCode.PARSE_TIMEOUT, "解析音频数据超时");
			errorMap.put(StatusCode.AUDIO_RECORD_IS_STOP, "录音停止了");
			errorMap.put(StatusCode.AUDIO_TRACK_NOT_INIT, "AudioTrack没有初始化");
			errorMap.put(StatusCode.INPUT_ERROR, "输入有错，输入的数据格式有误");
			errorMap.put(StatusCode.WRITE_EXCEPTION, "写入音频数据异常");
			errorMap.put(StatusCode.CANNOT_PLAY_TRACK, "不能播放音频");
			errorMap.put(StatusCode.CHECK_FAILED, "数据校验错误");
			errorMap.put(StatusCode.DEVICE_RECV_ERR, "单片机一直要求重发数据");
			errorMap.put(StatusCode.MUST_NOT_CMD_NORMAL_REQUEST, "单片机不应该给手机发送CMD_NORMAL_REQUEST命令");
			errorMap.put(StatusCode.INTERRPUTED, "工作被中断了");
			errorMap.put(StatusCode.PROTOCAL_ERR, "在保证数据接受完全正确的前提下，协议错误，任何与协议不符的错误");
			errorMap.put(StatusCode.NO_INPUT, "输入数据空");
			errorMap.put(StatusCode.AUDIO_VOLUME_LIMITED, "媒体音量被系统强制减弱，请到设置里修改媒体音量至最大值");

			errorMap.put(StatusCode.ERROR_FORMAL_PARAMETER, "ERROR_FORMAL_PARAMETER");
			errorMap.put(StatusCode.ERROR_DECRYPT_TCK, "ERROR_DECRYPT_TCK");
			errorMap.put(StatusCode.ERROR_SELECT_KEY_FILE, "ERROR_SELECT_KEY_FILE");
			errorMap.put(StatusCode.ERROR_UPDATE_TCK, "ERROR_UPDATE_TCK");
			errorMap.put(StatusCode.ERROR_CREATE_KEY_FILE, "ERROR_CREATE_KEY_FILE");
			errorMap.put(StatusCode.ERROR_READ_TCK, "ERROR_READ_TCK");
			errorMap.put(StatusCode.ERROR_DECRYPT_TRACK_KEY, "ERROR_DECRYPT_TRACK_KEY");
			errorMap.put(StatusCode.ERROR_TRACK_ENCRYPT_BY_TRACK_KEY, "ERROR_TRACK_ENCRYPT_BY_TRACK_KEY");
			errorMap.put(StatusCode.ERROR_CHECK_TRACK_KEY, "ERROR_CHECK_TRACK_KEY");
			errorMap.put(StatusCode.ERROR_UPDATE_TRACK_KEY, "ERROR_UPDATE_TRACK_KEY");
			errorMap.put(StatusCode.ERROR_DECRYPT_PIN_KEY, "ERROR_DECRYPT_PIN_KEY");
			errorMap.put(StatusCode.ERROR_ENCRYPT_BY_PIN_KEY, "ERROR_ENCRYPT_BY_PIN_KEY");
			errorMap.put(StatusCode.ERROR_CHECK_PIN_KEY, "ERROR_CHECK_PIN_KEY");
			errorMap.put(StatusCode.ERROR_UPDATE_PIN_KEY, "ERROR_UPDATE_PIN_KEY");
			errorMap.put(StatusCode.ERROR_DECRYPT_MAC_KEY, "ERROR_DECRYPT_MAC_KEY");
			errorMap.put(StatusCode.ERROR_ENCRYPT_BY_MAC_KEY, "ERROR_ENCRYPT_BY_MAC_KEY");
			errorMap.put(StatusCode.ERROR_CHECK_MAC_KEY, "ERROR_CHECK_MAC_KEY");
			errorMap.put(StatusCode.ERROR_UPDATE_MAC_KEY, "ERROR_UPDATE_MAC_KEY");
			errorMap.put(StatusCode.ERROR_CHALLENGE_LENGTH, "ERROR_CHALLENGE_LENGTH");
			errorMap.put(StatusCode.ERROR_AMOUNT_LENGTH, "ERROR_AMOUNT_LENGTH");
			errorMap.put(StatusCode.ERROR_EXTRA_LENGTH, "ERROR_EXTRA_LENGTH");
			errorMap.put(StatusCode.ERROR_ESC_KEY, "ERROR_ESC_KEY");
			errorMap.put(StatusCode.ERROR_ESC_APP, "ERROR_ESC_APP");
			errorMap.put(StatusCode.ERROR_TIMEOUT, "ERROR_TIMEOUT");
			errorMap.put(StatusCode.ERROR_PAY_METHOD, "ERROR_PAY_METHOD");
			errorMap.put(StatusCode.ERROR_GET_PIN, "ERROR_GET_PIN");
			errorMap.put(StatusCode.ERROR_PIN_LENGTH, "ERROR_PIN_LENGTH");
			errorMap.put(StatusCode.ERROR_NO_ICCARD, "ERROR_NO_ICCARD");
			errorMap.put(StatusCode.ERROR_RESET_ICCARD, "ERROR_RESET_ICCARD");
			errorMap.put(StatusCode.ERROR_AMOUNT_COMPRESSION, "ERROR_AMOUNT_COMPRESSION");
			errorMap.put(StatusCode.ERROR_AMOUNT_TRANSFORM_SHOW, "ERROR_AMOUNT_TRANSFORM_SHOW");
			errorMap.put(StatusCode.ERROR_GET_CHALLENGE, "ERROR_GET_CHALLENGE");
			errorMap.put(StatusCode.ERROR_55_ENCRYPT_BY_TRACK_KEY, "ERROR_55_ENCRYPT_BY_TRACK_KEY");
			errorMap.put(StatusCode.ERROR_SYS_TIME, "ERROR_SYS_TIME");
			errorMap.put(StatusCode.ERROR_ENCRYPT_BY_PACK_KEY, "ERROR_ENCRYPT_BY_PACK_KEY");
		}
	}
}
