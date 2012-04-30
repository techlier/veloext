/**
 * Velocity Directive Extensions.
 * Copyright (c) 2009-2012 Techlier Inc. All rights reserved.
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
 * https://github.com/techlier/veloext
 */

* Velocity Directive Extensions の使い方
+ velocity-1.2.jarをWEB-INF/lib等に配置します。
+ velocity.propertiesに以下の設定を追加してください。
 userdirective=jp.techlier.extensions.velocity.directive.Defvar
 userdirective=jp.techlier.extensions.velocity.directive.Defconst
 userdirective=jp.techlier.extensions.velocity.directive.Import
 userdirective=jp.techlier.extensions.velocity.directive.Displace
 userdirective=jp.techlier.extensions.velocity.directive.Apply
 userdirective=jp.techlier.extensions.velocity.directive.Block
 userdirective=jp.techlier.extensions.velocity.directive.Prepend
 userdirective=jp.techlier.extensions.velocity.directive.Append
 userdirective=jp.techlier.extensions.velocity.directive.Nil
 userdirective=jp.techlier.extensions.velocity.directive.Nop

----
** #defvar($var, value)
指定された変数が未定義の場合にのみ、新しい変数を定義します。

defvarディレクティブは、
 #if(!$var)
 #set($var = 'value')
 #end
と同等の結果を生じます。

*** 記述例）
- template...
 #defvar($var, 'value')
 var = $var
 #defvar($var, 'this statememnt is not effective')
 var = $var
 #set($var = 'value is changed')
 var = $var

- output...
 var = value
 var = value
 var = value is changed

----
** #defconst($var, value)
代入不可能な定数値を定義します。

*** 記述例)
- template...
 #defconst($CONSTANT, 'immutable')
 #set($CONSTANT = 'this statement is not effective')
 const = $CONSTANT

- output...
 const = immutable

----
** #import(template)
相対パスによるテンプレート指定が可能な、parseディレクティブの変形です。

----
** #displace(template)
指定されたテンプレートが存在しない場合には#displace ～ #endで囲まれたブロックをパースします。

*** 記述例）
- template...
 #displace('import.vm')
 import.vmが存在しない場合には、このブロックの内容が出力される。
 import.vmが存在する場合には、import.vmの内容が出力され、このブロックは無視される。
 #end

----
** #apply(template)
指定されたテンプレート内で定義されたblockディレクティブを、
#apply ~ #endで定義されたblockディレクティブの内容で置き換えます。

*** 記述例）
- template...
 #apply('base.vm')
 #block('block1')
 base.vmで定義されたblock1が、ここで記述された内容に置き換えられる。
 #end
 blockディレクティブ以外は出力されない。
 #end

- base.vm...
 以下のブロックが置き換えられる。
 #block('block1')
 オリジナルのブロック1
 #end
 #block('block2')
 オリジナルのブロック2
 #end

- output...
 以下のブロックが置き換えられる。
 base.vmで定義されたblock1が、ここで記述された内容に置き換えられる。
 オリジナルのブロック2

----
** #block(id)
再定義可能なブロックを定義します。
applyディレクティブと組み合わせることで、テンプレートの部分的な差し替えを可能とします。

*** options
- directive.block.late.rendering
velocity.propertiesでdirective.block.late.renderingを指定することにより、
#block()の展開タイミングを変更することができます。
true = 上書きされるblockが出現した時点のcontextに基づいて展開を行う。
false = 上書きするblockが出現した時点のcontextに基づいて展開を行う。(default)

*** 記述例）
- template...
 #set($file = 'block-example-extend.vm')
 #apply('block-example-base.vm')
 #block('block1')
 #set($var1 = 'extended')
 #defvar($var2, 'extended')
 block1@extend:
   file = $file
   var0 = $var0
   var1 = $var1
   var2 = $var2
 #end
 #end

- base.vm...
 #set($file = 'block-example-base.vm')
 #set($var0 = 'defined')
 #set($var1 = 'base')
 #defvar($var2, 'base')
 #block('block0')
 block0@base:
   file = $file
   var0 = $var0
   var1 = $var1
   var2 = $var2
 #end
 #block('block1')
 block1@base:
 #end
 #block('block2')
 block2@base:
   file = $file
   var0 = $var0
   var1 = $var1
   var2 = $var2
 #end

- output if directive.block.late.rendering = false(is default)...
 block0@base:
   file = block-example-base.vm
   var0 = defined
   var1 = base
   var2 = extended
 block1@extend:
   file = block-example-extend.vm
   var0 = $var0
   var1 = extended
   var2 = extended
 block2@base:
   file = block-example-base.vm
   var0 = defined
   var1 = base
   var2 = extended

- output if directive.block.late.rendering = true...
 block0@base:
   file = block-example-base.vm
   var0 = defined
   var1 = base
   var2 = base
 block1@extend:
   file = block-example-base.vm
   var0 = defined
   var1 = extended
   var2 = base
 block2@base:
   file = block-example-base.vm
   var0 = defined
   var1 = extended
   var2 = base

----
** #prepend(id)
指定されたblockの前にテンプレートを挿入します。

*** 記述例）
- template...
 #apply('base.vm')
 #prepend('block1')
 block1@prepend:
 #end
 #end

- output...
 block1@prepend:
 block1@base:

----
** #append(id)
指定されたblockの後ろにテンプレートを挿入します。

*** 記述例）
- template...
 #apply('base.vm')
 #append('block1')
 block1@append:
 #end
 #end

- output...
 block1@base:
 block1@append:

----
** #nil()
#nil ~ #endで囲まれた部分のパース結果を出力しません。

*** 記述例）
- template...
 #nil()
 このブロックは出力されないが、パースは行われる。
 #set($var = 'value')
 #end
 var = $var

- output...
 var = value

[EOF]

