/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010 SonarSource and Akram Ben Aissi
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.php.checks;

import org.junit.Test;
import org.sonar.php.PHPAstScanner;
import org.sonar.plugins.php.CheckTest;
import org.sonar.plugins.php.TestUtils;
import org.sonar.squidbridge.api.SourceFile;

public class TooManyReturnCheckTest extends CheckTest {

  private TooManyReturnCheck check = new TooManyReturnCheck();

  @Test
  public void defaultValue() throws Exception {
    SourceFile file = PHPAstScanner.scanSingleFile(TestUtils.getCheckFile("TooManyReturnCheck.php"), check);

    checkMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Reduce the number of returns of this function 4, down to the maximum allowed 3.")
      .noMore();
  }

  @Test
  public void custom() throws Exception {
    check.max = 2;

    SourceFile file = PHPAstScanner.scanSingleFile(TestUtils.getCheckFile("TooManyReturnCheck.php"), check);
    checkMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Reduce the number of returns of this function 4, down to the maximum allowed " + +check.max + ".")
      .next().atLine(16)
      .next().atLine(25)
      .noMore();
  }
}
