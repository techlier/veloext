/**
 * Velocity Directive Extentions.
 * Copyright (c) 2009-2010 Techlier Inc. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Project Location:
 * http://sourceforge.jp/projects/veloext/
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: readme.txt 11 2010-09-12 11:09:57Z kazuhide $
 */

Velocity Directive Extentions の使い方
----
#defvar($var, value)

指定された変数が未定義の場合にのみ、新しい変数を定義します。

記述例）

template...
#defvar($var, 'value')
$var
#defvar($var, 'this statememnt is not effective')
$var
#set($var = 'value is changed')
$var

output...
value
value
value is changed
defvarディレクティブは、
#if(!$var)
#set($var = 'value')
#end
と同等の結果を生じます。

----
#defconst($var, value)

代入不可能な定数値を定義します。
記述例)

template...
#defconst($CONSTANT, 'immutable')
#set($CONSTANT = 'this statement is not effective')
$CONSTANT

output...
immutable

----
#import(template)

相対パスによるテンプレート指定が可能な、parseディレクティブの変形です。

----
#displace(template)

指定されたテンプレートが存在しない場合には#displace ～ #endで囲まれたブロックをパースします。
記述例）

template...
#displace('import.vm')
import.vmが存在しない場合には、このブロックの内容が出力される。
import.vmが存在する場合には、import.vmの内容が出力され、
このブロックは無視される。
#end

----
#apply(template)

指定されたテンプレート内で定義されたblockディレクティブを、
#apply ~ #endで定義されたblockディレクティブの内容で置き換えます。
記述例）

template...
#apply('base.vm')
#block('block1')
base.vmで定義されたblock1が、ここで記述された内容に置き換えられる。
#end
blockディレクティブ以外は出力されない。
#end

base.vm...
以下のブロックが置き換えられる。
#block('block1')
オリジナルのブロック1
#end
#block('block2')
オリジナルのブロック2
#end

output...
以下のブロックが置き換えられる。
base.vmで定義されたblock1が、ここで記述された内容に置き換えられる。
オリジナルのブロック2

----
#block(id)

再定義可能なブロックを定義します。
applyディレクティブと組み合わせることで、テンプレートの部分的な差し替えを可能とします。
directive.block.late.rendering

velocity.propertiesでdirective.block.late.renderingを指定することにより、
#block()の展開タイミングを変更することができます。
true = 上書きされるblockが出現した時点のcontextに基づいて展開を行う。
false = 上書きするblockが出現した時点のcontextに基づいて展開を行う。(default)
記述例）

template...
#apply('base.vm')
#set($var1 = 'extended')
#block('block')
$var1 = extended
$!var2 = undefined
#end
#end

base.vm...
#set($var1 = 'base')
#set($var2 = 'defined')
$var1 = base
$!var2 = defined
#block('block')
$var1 = base
$!var2 = defined
#end

output if directive.block.late.rendering = true...
base = base
defined = defined
base = extended
defined = undefined

output if directive.block.late.rendering = false...
base = base
defined = defined
extended = extended
= undefined

----
#nil()

#nil ~ #endで囲まれた部分のパース結果を出力しません。
記述例）

template...
#nil()
このブロックは出力されないが、パースは行われる。
#set($var = 'value')
#end
$var

output...
value

