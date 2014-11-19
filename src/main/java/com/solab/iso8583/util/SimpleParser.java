/*
 * j8583 A Java implementation of the ISO8583 protocol
 * Copyright (C) 2007 Enrique Zamudio Lopez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package com.solab.iso8583.util;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoValue;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;

/** A simple command-line program that reads a configuration file to set up a MessageFactory
 * and parse messages read from STDIN.
 *
 * @author Enrique Zamudio
 *         Date: 20/06/12 02:11
 */
public class SimpleParser {

    private static BufferedReader reader;

    private static String getMessage() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(System.in));
        }
        System.out.println("Paste your ISO8583 message here (no ISO headers): ");
        return reader.readLine();
    }

    public static void main(String [] args) throws IOException, ParseException {
        final MessageFactory<IsoMessage> mf = new MessageFactory<IsoMessage>();
        if (args.length == 0) {
            ConfigParser.configureFromDefault(mf);
        } else {
            if (System.console() != null) {
                System.console().printf("Attempting to configure MessageFactory from %s...%n", args[0]);
            }
            String url = args[0];
            if (url.contains("://")) {
                ConfigParser.configureFromUrl(mf, new URL(args[0]));
            } else {
                ConfigParser.configureFromUrl(mf, new File(url).toURI().toURL());
            }
        }
        //Now read messages in a loop
        String a = getMessage();
//        String a = byteArry2HexString(getMessage().getBytes());
        String line = new String(new char[] {0xF0, 0xF8, 0xF0, 0xF0, 0xC2, 0x20, 0x00, 0x00, 0x80, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF0, 0xF5, 0xF9, 0xF1, 0xF1, 0xF0, 0xF9, 0xF1, 0xF0, 0xF2, 0xF2, 0xF1, 0xF0, 0xF0, 0xF5, 0xF3, 0xF0, 0xF0, 0xF0, 0xF0, 0xF3, 0xF6, 0xF1, 0xF0, 0xF6, 0xF0, 0xF0, 0xF1, 0xF1, 0xF0, 0xF9, 0xF2, 0xF7, 0xF0});
        byte[] z = new byte[] {(byte) 0xF0, (byte) 0xF8, (byte) 0xF1, (byte) 0xF0, (byte) 0xC2, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF0, (byte) 0xF5, (byte) 0xF9, (byte) 0xF1, (byte) 0xF1, (byte) 0xF0, (byte) 0xF9, (byte) 0xF1, (byte) 0xF0, (byte) 0xF2, (byte) 0xF2, (byte) 0xF1, (byte) 0xF0, (byte) 0xF0, (byte) 0xF5, (byte) 0xF3, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF3, (byte) 0xF6, (byte) 0xF1, (byte) 0xF0, (byte) 0xF6, (byte) 0xF0, (byte) 0xF0, (byte) 0xF1, (byte) 0xF1, (byte) 0xF0, (byte) 0xF9, (byte) 0xF0, (byte) 0xF0, (byte) 0xF2, (byte) 0xF7, (byte) 0xF0};
        
//        String line = a;
        while (line != null && line.length() > 0) {
        	mf.setUseBinaryMessages(true);
//        	mf.setUseBinaryBitmap(true);
            IsoMessage m = mf.parseMessage(z, 0);
            
            if (m != null) {
                System.out.printf("Message type: %04x%n", m.getType());
                System.out.println("FIELD TYPE    VALUE");
                for (int i = 2; i <= 128; i++) {
                    IsoValue<?> f = m.getField(i);
                    if (f != null) {
                        System.out.printf("%5d %-6s [", i, f.getType());
                        System.out.print(f.toString());
                        System.out.println(']');
                    }
                }
            }
            line = getMessage();
        }
    }
    
    public static String byteArry2HexString(byte[] byteArray) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			char byte1 = (char) (byteArray[i]);
			if (byte1 < 0)
				byte1 += 128;
			int hi = (int) ((byte1 & 0xF0) >> 4);
			int lo = (int) (byte1 & 0x0F);

			char chi;
			char clo;

			if (hi < 10) {
				chi = (char) (48 + hi);
			} else {
				chi = (char) (55 + hi);
			}

			if (lo < 10) {
				clo = (char) (48 + lo);
			} else {
				clo = (char) (55 + lo);
			}

			sb.append(chi).append(clo);
		}
		return sb.toString();

	}
}
