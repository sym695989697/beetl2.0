package org.beetl.core.util;

import static org.beetl.core.util.MethodMatchConf.BIGDECIMAL_CONVERT;
import static org.beetl.core.util.MethodMatchConf.DOUBLE_CONVERT;
import static org.beetl.core.util.MethodMatchConf.FLOAT_CONVERT;
import static org.beetl.core.util.MethodMatchConf.INT_CONVERT;
import static org.beetl.core.util.MethodMatchConf.LONG_CONVERT;
import static org.beetl.core.util.MethodMatchConf.NO_CONVERT;
import static org.beetl.core.util.MethodMatchConf.SHORT_CONVERT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class ObjectUtil
{
	public static Object copy(Object o)
	{
		if (o instanceof java.io.Serializable)
		{
			try
			{
				ByteArrayOutputStream bs = new ByteArrayOutputStream(128);
				ObjectOutputStream dos = new ObjectOutputStream(bs);
				dos.writeObject(o);
				ByteArrayInputStream is = new ByteArrayInputStream(bs.toByteArray());
				ObjectInputStream ios = new ObjectInputStream(is);
				Object copy = ios.readObject();
				return copy;

			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	public static String getMethod(String attrName)
	{
		StringBuilder mbuffer = new StringBuilder("get");
		mbuffer.append(attrName.substring(0, 1).toUpperCase()).append(attrName.substring(1));
		return mbuffer.toString();
	}

	public static String getIsMethod(String attrName)
	{
		StringBuilder mbuffer = new StringBuilder("is");
		mbuffer.append(attrName.substring(0, 1).toUpperCase()).append(attrName.substring(1));
		return mbuffer.toString();
	}

	/**看给定的参数是否匹配给定方法的前parameterCount参数 joelli
	 * @param method 
	 * @param parameterType 输入的参数
	 * @param parameterCount 如果为-1，则是精确匹配，输入参数与方法得参数个数必须一致
	 * @return
	 */
	public static MethodMatchConf match(Method method, Class[] parameterType, int parameterCount)
	{
		Class[] paras = method.getParameterTypes();
		if (parameterCount == -1)
		{
			if (parameterType.length != paras.length)
			{
				return null;
			}
			parameterCount = parameterType.length;
		}

		if (parameterType.length > parameterCount)
		{
			return null;
		}

		int[] convert = new int[parameterCount];

		boolean isMatch = true;

		for (int j = 0; j < parameterType.length; j++)
		{

			if (parameterType[j] == null)
			{
				// 认为匹配
				convert[j] = NO_CONVERT;
				continue;
			}

			if (parameterType[j] == paras[j])
			{
				convert[j] = NO_CONVERT;
				continue;
			}

			if (paras[j] == Object.class)
			{

				convert[j] = NO_CONVERT;

				continue;
			}
			else if (paras[j].isAssignableFrom(parameterType[j]))
			{
				convert[j] = NO_CONVERT;
				continue;
			}

			else if (paras[j].isPrimitive())
			{

				if (paras[j] == int.class)
				{
					convert[j] = INT_CONVERT;
				}
				else if (paras[j] == long.class)
				{
					convert[j] = LONG_CONVERT;
				}
				else if (paras[j] == double.class)
				{
					convert[j] = DOUBLE_CONVERT;
				}
				else if (paras[j] == float.class)
				{
					convert[j] = FLOAT_CONVERT;
				}
				else if (paras[j] == short.class)
				{
					convert[j] = SHORT_CONVERT;
				}
			}
			else if (Number.class.isAssignableFrom(paras[j]))
			{
				if (paras[j] == Integer.class)
				{
					convert[j] = INT_CONVERT;
				}
				else if (paras[j] == Long.class)
				{
					convert[j] = LONG_CONVERT;
				}
				else if (paras[j] == Double.class)
				{
					convert[j] = DOUBLE_CONVERT;
				}
				else if (paras[j] == Float.class)
				{
					convert[j] = FLOAT_CONVERT;
				}
				else if (paras[j] == Short.class)
				{
					convert[j] = SHORT_CONVERT;
				}
				else if (paras[j] == BigDecimal.class)
				{
					convert[j] = BIGDECIMAL_CONVERT;
				}
				else
				{
					throw new RuntimeException("不支持的类型转化");
				}
			}
			else if (paras[j] == Boolean.class)
			{
				if (parameterType[j] == boolean.class || parameterType[j] == Boolean.class)
				{
					convert[j] = NO_CONVERT;
				}
			}
			else if (paras[j] == boolean.class)
			{
				if (parameterType[j] == boolean.class || parameterType[j] == Boolean.class)
				{
					convert[j] = NO_CONVERT;
				}
			}
			else
			{
				isMatch = false;
			}
		}

		if (isMatch)
		{

			MethodMatchConf mc = new MethodMatchConf();
			mc.method = method;
			mc.convert = convert;
			for (int c : convert)
			{
				if (c != 0)
				{
					mc.isNeedConvert = true;
					break;
				}
			}
			if (parameterType.length != parameterCount)
			{
				mc.isExactMatch = false;
			}
			return mc;
		}
		else
		{
			return null;
		}

	}

	//	public void call(long c, Object k)
	//	{
	//		System.out.println(c + "" + k);
	//	}
	//
	//	public static void main(String[] args) throws Exception
	//	{
	//		ObjectUtil o = new ObjectUtil();
	//		Method m = o.getClass().getMethod("call", new Class[]
	//		{ long.class, Object.class });
	//		m.invoke(o, new Object[]
	//		{ new Double(2), "hi" });
	//	}

}
