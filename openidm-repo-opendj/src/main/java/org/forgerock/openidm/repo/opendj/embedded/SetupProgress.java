/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 ForgeRock AS. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 */

package org.forgerock.openidm.repo.opendj.embedded;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Setup progress
 */
public class SetupProgress {
    static private Writer writer = null;
    static private String bundleName = "amsessiondbsetup";

    static private OutputStream out = null;
    static private String encoding = System.getProperty("file.encoding");
    static private ResourceBundle bundle = ResourceBundle.getBundle(
        bundleName, Locale.getDefault());

    /**
     * Returns writer associated with reporting progress to enduser.
     *
     * @return writer associated with reporting progress to enduser.
     */
    public static Writer getWriter() {
        return writer;
    }

    /**
     * Sets locale
     *
     * @param locale Locale.
     */
    static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle(bundleName, locale);
    }

    /**
     * Returns OutputStream associated with reporting progress to enduser.
     *
     * @return OutputStream associated with reporting progress to enduser.
     */
    public static OutputStream getOutputStream() {
        return out;
    }

    /**
      * Sets setup writer.
      * @param w Setup writer to be used.
      */
    public static void setWriter(Writer w) {
        writer = w;
        if (writer == null) {
            out = null;
            return;
        }

        out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                writer.write(String.valueOf((char) b));
                writer.flush();
            }
            @Override
            public void write(byte[] b) throws IOException {
                writer.write(new String(b, encoding));
                writer.flush();
            }
            @Override
            public void write(byte[] b, int off, int len)
                throws IOException {

                writer.write(new String(b, off, len, encoding));
                writer.flush();
            }
            @Override
            public void flush() throws IOException {
                writer.flush();
            }
        };
    }

    /**
     * Sets setup OututStream.
     * @param ostr OutputStream  to be used.
     */
    public static void setOutputStream(OutputStream  ostr) {
        out = ostr;
    }

    /**
      * Reports beginning of an operation.
      *
      * @param str i18n key to be printed
      * @param param Object to be printed.
      */
    public static void reportStart(String str, Object[] param) { 
        writeProgressText(str, param, false);
    }

    /**
     * Reports end of an operation.
     * @param str i18n key to be printed
     * @param param Object to be printed.
     */
    public static void reportEnd(String str, Object[] param) {
        writeProgressText(str, param, true);
    }

    private static void writeProgressText(
        String str,
        Object[] param,
        boolean newline
    ) {
        String istr = null;
        
        try {
            istr = bundle.getString(str);
            if ((param != null) && (param.length > 0)) {
                istr = MessageFormat.format(istr, param);
            }
        } catch (MissingResourceException e) {
            istr = str;
        }

        reportDebug(istr, newline);
    }

    private static void reportDebug(String istr, boolean newline) {
        try {
            if (newline) {
                SetupLog.getInstance().write(istr + "\n");
            } else {
                SetupLog.getInstance().write(istr);
            }
            if (writer != null) {
                if (newline) {
                    istr += "\n";
                }
                
                writer.write(istr);
                writer.flush();
            }
        } catch (IOException ex) {
            //ignore
        }
    }    
}
