/*
 * Copyright 2020 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UsePropertyAccessSyntax")

package com.squareup.wire

import com.google.protobuf.Any
import com.google.protobuf.DescriptorProtos
import com.google.protobuf.FieldOptions
import com.google.protobuf.util.JsonFormat
import com.squareup.moshi.Moshi
import com.squareup.wire.proto3.requiredextension.RequiredExtension
import com.squareup.wire.proto3.requiredextension.RequiredExtensionMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import squareup.proto3.pizza.BuyOneGetOnePromotion
import squareup.proto3.pizza.FreeGarlicBreadPromotion
import squareup.proto3.pizza.Pizza
import squareup.proto3.pizza.PizzaDelivery
import squareup.proto3.pizza.PizzaOuterClass

class Proto3WireProtocCompatibilityTests {
  // Note: this test mostly make sure we compile required extension without failing.
  @Test fun protocAndRequiredExtensions() {
    val wireMessage = RequiredExtensionMessage("Yo")

    val googleMessage = RequiredExtension.RequiredExtensionMessage.newBuilder()
        .setStringField("Yo")
        .build()

    assertThat(wireMessage.encode()).isEqualTo(googleMessage.toByteArray())

    // Although the custom options has no label, it shouldn't be "required" to instantiate
    // `FieldOptions`. We should not fail.
    assertThat(DescriptorProtos.FieldOptions.newBuilder().build()).isNotNull()
    assertThat(FieldOptions()).isNotNull()
  }

  @Test fun protocJson() {
    val pizzaDelivery = PizzaOuterClass.PizzaDelivery.newBuilder()
        .setAddress("507 Cross Street")
        .addPizzas(PizzaOuterClass.Pizza.newBuilder()
            .addToppings("pineapple")
            .addToppings("onion")
            .build())
        .setPromotion(Any.pack(PizzaOuterClass.BuyOneGetOnePromotion.newBuilder()
            .setCoupon("MAUI")
            .build()))
        .build()

    val json = """
        |{
        |  "address": "507 Cross Street",
        |  "pizzas": [{
        |    "toppings": ["pineapple", "onion"]
        |  }],
        |  "promotion": {
        |    "@type": "type.googleapis.com/squareup.proto3.pizza.BuyOneGetOnePromotion",
        |    "coupon": "MAUI"
        |  }
        |}
        """.trimMargin()

    val typeRegistry = JsonFormat.TypeRegistry.newBuilder()
        .add(PizzaOuterClass.BuyOneGetOnePromotion.getDescriptor())
        .add(PizzaOuterClass.FreeGarlicBreadPromotion.getDescriptor())
        .build()

    val jsonPrinter = JsonFormat.printer()
        .usingTypeRegistry(typeRegistry)
    assertThat(jsonPrinter.print(pizzaDelivery)).isEqualTo(json)

    val jsonParser = JsonFormat.parser().usingTypeRegistry(typeRegistry)
    val parsed = PizzaOuterClass.PizzaDelivery.newBuilder()
        .apply { jsonParser.merge(json, this) }
        .build()
    assertThat(parsed).isEqualTo(pizzaDelivery)
  }

  @Test fun wireJson() {
    val wireMessage = PizzaDelivery(
        address = "507 Cross Street",
        pizzas = listOf(Pizza(toppings = listOf("pineapple", "onion"))),
        promotion = AnyMessage.pack(BuyOneGetOnePromotion(coupon = "MAUI"))
    )
    val json = """
        |{
        |  "address": "507 Cross Street",
        |  "pizzas": [
        |    {
        |      "toppings": [
        |        "pineapple",
        |        "onion"
        |      ]
        |    }
        |  ],
        |  "promotion": {
        |    "@type": "type.googleapis.com/squareup.proto3.pizza.BuyOneGetOnePromotion",
        |    "coupon": "MAUI"
        |  }
        |}
        """.trimMargin()

    val moshi = Moshi.Builder()
        .add(WireJsonAdapterFactory()
            .plus(BuyOneGetOnePromotion.ADAPTER, FreeGarlicBreadPromotion.ADAPTER))
        .build()

    val jsonAdapter = moshi.adapter(PizzaDelivery::class.java).indent("  ")
    assertThat(jsonAdapter.toJson(wireMessage)).isEqualTo(json)
    assertThat(jsonAdapter.fromJson(json)).isEqualTo(wireMessage)
  }

  // Wire -> Google, Google -> Wire
  //

  // Nice error on unrecognized @type
  // Nice error on absent @type
  // Nice error on unregistered type
}
