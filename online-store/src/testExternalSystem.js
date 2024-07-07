import {ExternalSystemService} from "@/ExternalSystemService";

export async function testService() {
    const service = new ExternalSystemService();

    try {
        const handshakeResponse = await service.handshake();
        console.log('Handshake Response:', handshakeResponse);
    } catch (error) {
        console.error('Service Error:', error);
    }
    try {


        const payResponse = await service.pay("1000", "USD", "2222333344445555", "4", "2021", "Israel Israelovice", "262", "20444444");
        console.log('Payment Response:', payResponse);
    } catch (error) {
        console.error('Service Error:', error);
    }
    try {


        const cancelPayResponse = await service.cancelPay("20123");
        console.log('Cancel Payment Response:', cancelPayResponse);

    } catch (error) {
        console.error('Service Error:', error);
    }
    try {


        const supplyResponse = await service.supply("Israel Israelovice", "Rager Blvd 12", "Beer Sheva", "Israel", "8458527");
        console.log('Supply Response:', supplyResponse);
    } catch (error) {
        console.error('Service Error:', error);
    }
    try {


        const cancelSupplyResponse = await service.cancelSupply("30525");
        console.log('Cancel Supply Response:', cancelSupplyResponse);

    } catch
        (error) {
        console.error('Service Error:', error);
    }
}

